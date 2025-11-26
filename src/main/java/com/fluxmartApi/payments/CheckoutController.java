package com.fluxmartApi.payments;

import com.fluxmartApi.auth.UserNotFoundException;
import com.fluxmartApi.cart.CartNotFoundException;
import com.fluxmartApi.order.CartEmptyException;
import com.fluxmartApi.order.OrderNotFoundException;
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
            return checkOutService.placeOrder(requestDto.getCartId(),requestDto.getPaymentMethod(),requestDto.getPhoneNumber());
    }

    @PostMapping("/webhook")
    public void handleWebHook(@RequestBody String payload,
                              @RequestHeader("Stripe-Signature") String sigHeader) {
        checkOutService.handleWebhookEvent(new WebhookEventRequest(payload, Map.of("stripe-signature", sigHeader)));
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

    @ExceptionHandler(OrderAlreadyUpdatedException.class)
    public ResponseEntity<?> handleOrderPaidException (){
        return ResponseEntity.status(HttpStatus.FOUND).body("Order  Already Updated");
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<?> handleOrderNotFoundException (){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order  Not Found");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handlePaymentServiceNotFoundException (){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Unsupported payment gateway: ");
    }
}
