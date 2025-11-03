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
    public ResponseEntity<?> addToCart(@PathVariable("cartId") UUID cartId, @RequestBody AddToCartRequest request){
        var response= cartService.addToCart(cartId,request.getProductId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{cartId}/items/{productId}")
    public ResponseEntity<?>updateCartItem(@PathVariable("cartId") UUID cartId,@PathVariable("productId")Integer productd,@RequestBody UpdateCartItemRequest request){
        var response=cartService.updateCartItem(cartId,productd,request.getQuantity());
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{cartId}/items/{productId}")
    public ResponseEntity<?>deleteCartItem(@PathVariable("cartId") UUID cartId,@PathVariable("productId")Integer productd){
        cartService.deleteCartItem(cartId,productd);
        return ResponseEntity.status(HttpStatus.GONE).body("Product Deleted");
    }

    @DeleteMapping("/{cartId}/items")
    public ResponseEntity<?>clearCart(@PathVariable("cartId") UUID cartId){
        cartService.clearCart(cartId);
        return ResponseEntity.ok().body("Cart Cleared");
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
