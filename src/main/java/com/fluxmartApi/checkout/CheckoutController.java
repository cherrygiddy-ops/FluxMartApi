package com.fluxmartApi.checkout;

import com.fluxmartApi.auth.UserNotFoundException;
import com.fluxmartApi.cart.CartNotFoundException;
import com.fluxmartApi.order.CartEmptyException;
import com.fluxmartApi.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/checkout")
public class CheckoutController {
    private final CheckOutService checkOutService;
    @PostMapping()
    public CheckoutResponseDto checkout(@RequestBody CheckoutRequestDto requestDto){
        return checkOutService.placeOrder(requestDto.getCartId());
    }

    @ExceptionHandler(CartEmptyException.class)
    public ResponseEntity<?> handleCartEmpty (){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cart is Empty");
    }

    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<?> handleCartNotFound (){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cart Not Found");
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFound (){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please Log In First");
    }
}
