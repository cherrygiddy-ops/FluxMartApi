package com.fluxmartApi.payments;

import com.fluxmartApi.auth.UserNotFoundException;
import com.fluxmartApi.cart.CartNotFoundException;
import com.fluxmartApi.order.CartEmptyException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/checkout")
public class CheckoutController {
    private final CheckOutService checkOutService;

    @PostMapping()
    public CheckoutResponseDto checkout(@RequestBody CheckoutRequestDto requestDto){
            return checkOutService.placeOrder(requestDto.getCartId());
    }

    @PostMapping("/webhook")
    public void   handleWebHook(@RequestBody() String payload, @RequestHeader()Map<String,String> singnatureHeader){
          checkOutService.handleWebhookEvent(new WebhookEventRequest(payload,singnatureHeader));

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

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<?> handlePaymentException (){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could Not Create A Checkout Session");
    }
}
