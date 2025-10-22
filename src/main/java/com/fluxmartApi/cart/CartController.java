package com.fluxmartApi.cart;

import com.fluxmartApi.products.CategoryNotFoundException;
import com.fluxmartApi.products.ProductNotFoundException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/carts")
public class CartController {
    private final CartService cartService;

    @PostMapping()
    public ResponseEntity<CartResponseDto>  createCart(){
        var response = cartService.createCart();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{cartId}")
    public CartResponseDto getCartDetails(@PathVariable("cartId")UUID cartId){
        return cartService.getCartDetails(cartId);
    }

    @PostMapping("/{cartId}/items")
    public ResponseEntity<?> addToCart(@PathVariable("cartId") UUID cartId,@Valid @RequestBody AddToCartRequest request){
        var response= cartService.addToCart(cartId,request);
        System.out.println(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<?> handleCartNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("cart not found");
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<?> handleProductNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
    }
}
