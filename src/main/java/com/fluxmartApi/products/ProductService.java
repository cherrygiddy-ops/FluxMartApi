package com.fluxmartApi.products;

import com.fluxmartApi.categories.CategoryRepository;
import com.fluxmartApi.imageKit.ImageService;
import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.models.FileCreateRequest;
import io.imagekit.sdk.models.results.Result;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final CategoryRepository categoryRepository;
    private final ProductsRepository productsRepository;
    private final ProductsMapper productsMapper;
    private  final ImageService imageService;


     public ProductsResponseDto  addProduct(ProductsRequestDto requestDto,MultipartFile image){
         var category = categoryRepository.findById(requestDto.getCategoryId())
                 .orElseThrow(CategoryNotFoundException::new);

         var product = productsMapper.toEntity(requestDto);
         product.setCategory(category);

         try {
             String imageUrl = imageService.uploadImage(image.getBytes(), image.getOriginalFilename());
             product.setImageUrl(imageUrl);
         } catch (Exception e) {
             throw new RuntimeException("Image upload failed", e);
         }

         productsRepository.save(product);
         return productsMapper.toDto(product);

     }

    public String saveImageLocally(MultipartFile file) {
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        Path uploadPath = Paths.get("uploads");

        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Path filePath = uploadPath.resolve(fileName);
        try {
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Return relative URL for frontend
        return "/uploads/" + fileName;
    }

    public String saveImageWithImageKit(MultipartFile file) {
        try {
            // Generate unique filename
            String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

            // Upload to ImageKit
            FileCreateRequest request = new FileCreateRequest(file.getBytes(), fileName);
            Result result = ImageKit.getInstance().upload(request);

            // Return CDN URL instead of local path
            return result.getUrl();
        } catch (Exception e) {
            throw new RuntimeException("Image upload failed", e);
        }
    }



    public ProductsResponseDto getProductsDetails(int id){
         var product = productsRepository.findById(id).orElseThrow(ProductNotFoundException::new);
        return  productsMapper.toDto(product);
     }
     public ProductsResponseDto getProductsDetailsByName(String name){
        var product = productsRepository.findByName(name).orElseThrow(ProductNotFoundException::new);
        return  productsMapper.toDto(product);
    }

    public Page<ProductsResponseDto> getAllProducts(Pageable pageable) {
        return productsRepository.findAll(pageable).map(productsMapper::toDto);
    }
    public Page<ProductsResponseDto> findByCategoryId(Byte categoryId, Pageable pageable) {
        return productsRepository
                .findByCategoryId(categoryId, pageable)
                .map(productsMapper::toDto);
    }
    public ProductPageResponse searchProducts(Byte categoryId, String keyword, Pageable pageable) {
        Page<ProductsEntity> products;

        if (categoryId != null && keyword != null && !keyword.isBlank()) {
            products = productsRepository.findByCategoryIdAndNameContainingIgnoreCase(categoryId, keyword, pageable);
        } else if (categoryId != null) {
            products = productsRepository.findByCategoryId(categoryId, pageable);
        } else if (keyword != null && !keyword.isBlank()) {
            products = productsRepository.findByNameContainingIgnoreCase(keyword, pageable);
        } else {
            products = productsRepository.findAll(pageable);
        }

        Page<ProductsResponseDto> dtoPage = products.map(productsMapper::toDto);

        // Wrap into ProductPage DTO
        ProductPageResponse response = new ProductPageResponse();
        response.setContent(dtoPage.getContent());
        response.setTotalPages(dtoPage.getTotalPages());
        response.setTotalElements(dtoPage.getTotalElements());
        response.setPageNumber(dtoPage.getNumber());
        response.setPageSize(dtoPage.getSize());

        return response;

    }
    public List<ProductsResponseDto> getSortedProducts(String sortBy) {
        if (!Set.of("categoryId","name","price").contains(sortBy))
            sortBy = "name";
        return productsRepository.findAll(Sort.by(sortBy).descending()).stream().map(productsMapper::toDto).toList();
    }

    public List<ProductsResponseDto> getPaginatedProducts ( Byte pageSize, Byte pageNumber){
        var pageRequest = PageRequest.of(pageNumber,pageSize);
        return productsRepository.findAll(pageRequest).stream().map(productsMapper::toDto).toList();
    }

    public void deleteProduct( Integer id){
        var product= productsRepository.findById(id).orElse(null);
        if (product== null)
            throw new ProductNotFoundException();
        productsRepository.delete(product);
    }

    @Transactional
    public ProductsResponseDto updateProduct(Integer id, UpdateProductRequest request) {
        var category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(CategoryNotFoundException::new);

        var product = productsRepository.findById(id)
                .orElseThrow(ProductNotFoundException::new);

        product.setName(request.getName());
        product.setDescriptions(request.getDescriptions());
        product.setQuantity(request.getQuantity());
        product.setPrice(request.getPrice());
        product.setCategory(category);

        try {
            String imageUrl = imageService.uploadImage(request.getImage().getBytes(), request.getImage().getOriginalFilename());
            product.setImageUrl(imageUrl);
        } catch (Exception e) {
            throw new RuntimeException("Image upload failed", e);
        }

        productsRepository.save(product);
        return productsMapper.toDto(product);
    }

}
