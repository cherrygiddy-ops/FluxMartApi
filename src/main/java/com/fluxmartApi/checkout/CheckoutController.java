package com.fluxmartApi.checkout;

import com.fluxmartApi.auth.UserNotFoundException;
import com.fluxmartApi.cart.CartNotFoundException;
import com.fluxmartApi.order.CartEmptyException;
import com.fluxmartApi.order.OrderService;
import com.stripe.exception.StripeException;
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
    public ResponseEntity<?> checkout(@RequestBody CheckoutRequestDto requestDto){
        try {
            var response= checkOutService.placeOrder(requestDto.getCartId());
            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            System.out.println(e.getMessage());
          return   ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("could not create a session");
        }
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
