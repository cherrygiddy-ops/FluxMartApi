package com.fluxmartApi.products;

import com.fluxmartApi.categories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final CategoryRepository categoryRepository;
    private final ProductsRepository productsRepository;
    private final ProductsMapper productsMapper;


     public ProductsResponseDto  addProduct(ProductsRequestDto requestDto){
             var category = categoryRepository.findById(requestDto.getCategoryId())
                     .orElseThrow(CategoryNotFoundException::new);

             var product = productsMapper.toEntity(requestDto);
             product.setCategory(category);

             List<ProductImage> images = new ArrayList<>();
             for (MultipartFile file : requestDto.getImageFiles()) {
                 String imagePath = saveImage(file);
                 ProductImage image = new ProductImage();
                 image.setImageUrl(imagePath);
                 image.setProduct(product);
                 images.add(image);
             }

             product.setImages(images);
             productsRepository.save(product);
         System.out.println(product);
             return productsMapper.toDto(product);

     }

    private String saveImage(MultipartFile file) {
        try {
            String folder = "uploads/";
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get(folder + filename);
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());
            return "/uploads/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image", e);
        }
    }

     public ProductsResponseDto getProductsDetails(int id){
         var product = productsRepository.findById(id).orElseThrow(ProductNotFoundException::new);
        return  productsMapper.toDto(product);
     }
    public List<ProductsResponseDto> getAllProducts(String sortBy) {
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

    public ProductsResponseDto updateProduct( Integer id,UpdateProductRequest request){
        var category = categoryRepository.findById(request.getCategoryId()).orElse(null);
        if (category==null) throw new CategoryNotFoundException();
        var product = productsRepository.findById(id).orElse(null);
        if (product==null) throw new ProductNotFoundException();

        product.setName(request.getName());
        product.setDescriptions(request.getDescriptions());
        product.setQuantity(request.getQuantity());
        product.setPrice(request.getPrice());

        if (request.getImageFiles() != null && !request.getImageFiles().isEmpty()) {
            product.getImages().clear(); // Remove old images (optional)
            List<ProductImage> newImages = request.getImageFiles().stream()
                    .map(file -> {
                        String path = saveImage(file);
                        ProductImage img = new ProductImage();
                        img.setImageUrl(path);
                        img.setProduct(product);
                        return img;
                    })
                    .collect(Collectors.toList());
            product.setImages(newImages);
        }

        productsRepository.save(product);
        return productsMapper.toDto(product);
    }

}
