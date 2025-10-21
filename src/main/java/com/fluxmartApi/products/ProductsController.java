package com.fluxmartApi.products;

import com.fluxmartApi.categories.CategoryRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/products")
public class ProductsController {

    private final CategoryRepository categoryRepository;
    private final ProductsMapper productsMapper;
    private final ProductsRepository productsRepository;


    @PostMapping
    public ResponseEntity<?>  addProduct(@Valid @RequestBody ProductsRequestDto requestDto){
    //validate category
        var category = categoryRepository.findById(requestDto.getCategoryId()).orElse(null);
        if (category == null)
            return ResponseEntity.badRequest().body("Cannot find the requested Category");
        var product= productsMapper.toEntity(requestDto);
        product.setCategory(category);

        productsRepository.save(product);

        return ResponseEntity.ok(product);
    }
    @GetMapping("/{productId}")
    public ResponseEntity<ProductsResponseDto> getProductDetails (@PathVariable(name = "productId") Integer id) {
        var product = productsRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("product not found"));
        var responsedto = productsMapper.toDto(product);
        return ResponseEntity.ok(responsedto);
    }


    @GetMapping()
    public ResponseEntity<?> getALLProducts (@RequestParam(name = "sort", required = false,defaultValue = "") String sortBy){
       if (!Set.of("categoryId","name","price").contains(sortBy))
           sortBy= "name";
    var products = productsRepository.findAll(Sort.by(sortBy).descending()).stream().map(productsMapper::toDto).toList();
    return ResponseEntity.ok(products);
    }
    @GetMapping("/pages")
    public ResponseEntity<?> getPaginatedProducts (@RequestParam(name = "pageSize") Byte pageSize,@RequestParam(name = "pageNumber") Byte pageNumber){
        var pageRequest = PageRequest.of(pageNumber,pageSize);
        var products = productsRepository.findAll(pageRequest).stream().map(productsMapper::toDto).toList();
        return ResponseEntity.ok(products);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable(name = "productId") Integer id){

        var product= productsRepository.findById(id).orElse(null);
        if (product== null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not Found");
        productsRepository.delete(product);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable(name = "productId") Integer id,@RequestBody UpdateProductRequest request){
        var category = categoryRepository.findById(request.getCategoryId()).orElse(null);
        if (category==null) return ResponseEntity.notFound().build();
        var product = productsRepository.findById(id).orElse(null);
        if (product==null) return ResponseEntity.notFound().build();

        productsMapper.updateProduct(request,product);
       product.setCategory(category);
        productsRepository.save(product);
        return ResponseEntity.ok(productsMapper.toDto(product));
    }
}
