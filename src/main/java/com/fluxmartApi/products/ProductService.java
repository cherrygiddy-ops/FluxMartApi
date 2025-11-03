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

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final CategoryRepository categoryRepository;
    private final ProductsRepository productsRepository;
    private final ProductsMapper productsMapper;


     public ProductsResponseDto  addProduct(ProductsRequestDto requestDto){
         var category = categoryRepository.findById(requestDto.getCategoryId()).orElse(null);
         if (category == null)
             throw new CategoryNotFoundException();
         var product= productsMapper.toEntity(requestDto);
         product.setCategory(category);
         productsRepository.save(product);
         return productsMapper.toDto(product);
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

        productsMapper.updateProduct(request,product);
        productsRepository.save(product);
        return productsMapper.toDto(product);
    }

}
