package com.fluxmartApi.payments;

import com.fluxmartApi.auth.UserNotFoundException;
import com.fluxmartApi.cart.CartNotFoundException;
import com.fluxmartApi.order.CartEmptyException;
import com.fluxmartApi.order.OrderNotFoundException;
import com.fluxmartApi.payments.stripe.CheckoutResponseDto;
import com.fluxmartApi.payments.stripe.StripePaymentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/checkout")
public class CheckoutController {
    private final CheckOutService checkOutService;
    private static final Logger log = LoggerFactory.getLogger(StripePaymentService.class);

    @PostMapping()
    public CheckoutResponseDto checkout(@RequestBody CheckoutRequestDto requestDto){
            return checkOutService.placeOrder(requestDto.getCartId(),requestDto.getPaymentGateway());
    }

    @PostMapping("/webhook")
    public void handleWebHook(@RequestBody String payload,
                              @RequestHeader("Stripe-Signature") String sigHeader) {
        log.info("Webhook endpoint hit, payload length={}, signature={}", payload.length(), sigHeader);
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
