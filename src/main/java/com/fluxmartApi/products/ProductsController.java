package com.fluxmartApi.products;

import com.fluxmartApi.categories.CategoryRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/products")
public class ProductsController {
   // private final ProductsMapper productsMapper;
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<?>  addProduct(@Valid @ModelAttribute ProductsRequestDto requestDto){
       var response= productService.addProduct(requestDto);
       return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/{productId}")
    public ProductsResponseDto getProductDetails (@PathVariable(name = "productId") Integer id) {
      return productService.getProductsDetails(id);
    }
    @GetMapping()
    public List<ProductsResponseDto> getALLProducts (@RequestParam(required = false) Byte categoryId,
                                                     @RequestParam(defaultValue = "0") int pageNumber,
                                                     @RequestParam(defaultValue = "10") int pageSize,
                                                     @RequestParam(defaultValue = "id,asc") String sort
    ){
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(parseSort(sort)));

        if (categoryId == null) {
            return productService.getAllProducts(pageable);
        }
        return productService.findByCategoryId(categoryId, pageable);

    }

    private Sort.Order parseSort(String sort) {
        String[] parts = sort.split(",");
        String property = parts[0];
        Sort.Direction direction = parts.length > 1 && parts[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        return new Sort.Order(direction, property);
    }

    public List<ProductsResponseDto> getSortedProducts (@RequestParam(name = "sort", required = false,defaultValue = "") String sortBy){
        return productService.getSortedProducts(sortBy);
    }

    @GetMapping("/pages")
    public List<ProductsResponseDto> getPaginatedProducts (@RequestParam(name = "pageSize") Byte pageSize, @RequestParam(name = "pageNumber") Byte pageNumber){
        return productService.getPaginatedProducts(pageSize,pageNumber);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable(name = "productId") Integer id){
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{productId}")
    public ProductsResponseDto updateProduct(@PathVariable(name = "productId") Integer id,@ModelAttribute UpdateProductRequest request){
        return productService.updateProduct(id,request);
    }

    @GetMapping("/search")
    public List<ProductsEntity> searchProducts(@RequestParam String keyword) {
        return productService.searchByKeyword(keyword);
    }


    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<?> handleProductNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("product not found");
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<?> handleCategoryNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("category not found");
    }

}
