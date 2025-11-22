package com.fluxmartApi.products;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/products")
public class ProductsController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<?>  addProduct(@ModelAttribute ProductsRequestDto form) {
        ProductsRequestDto dto = new ProductsRequestDto();
        dto.setName(form.getName());
        dto.setDescriptions(form.getDescriptions());
        dto.setCategoryId(form.getCategoryId());
        dto.setPrice(form.getPrice());
        dto.setQuantity(form.getQuantity());

        var response= productService.addProduct(dto, form.getImage());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @RequestMapping("/products")
    public void logHeaders(HttpServletRequest request) {
        Collections.list(request.getHeaderNames()).forEach(name -> System.out.println(name + ": " + request.getHeader(name)));
    }

    @GetMapping("/{productId}")
    public ProductsResponseDto getProductDetails (@PathVariable(name = "productId") Integer id) {
      return productService.getProductsDetails(id);
    }
//    @GetMapping("/{productName}")
//    public ProductsResponseDto getProductDetailsWithName (@PathVariable(name = "productName") String name) {
//        return productService.getProductsDetailsByName(name);
//    }
    @GetMapping()
    public ProductPageResponse getProducts(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Byte categoryId,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String keyword
    ) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy != null ? sortBy : "id"));

        return productService.searchProducts(categoryId, keyword, pageable);

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

//    @GetMapping("/search")
//    public List<ProductsEntity> searchProducts(@RequestParam String keyword) {
//        return productService.searchByKeyword(keyword);
//    }


    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<?> handleProductNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("product not found");
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<?> handleCategoryNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("category not found");
    }

}
