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
    public List<ProductsResponseDto> getALLProducts (@RequestParam(name = "sort", required = false,defaultValue = "") String sortBy){
        return productService.getAllProducts(sortBy);
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

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<?> handleProductNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("product not found");
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<?> handleCategoryNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("category not found");
    }

}
