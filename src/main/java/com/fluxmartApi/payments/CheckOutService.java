package com.fluxmartApi.payments;

import com.fluxmartApi.auth.AuthService;
import com.fluxmartApi.cart.CartNotFoundException;
import com.fluxmartApi.cart.CartRepository;
import com.fluxmartApi.cart.CartService;
import com.fluxmartApi.order.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CheckOutService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final AuthService authService;
    private final CartService cartService;
    private final PaymentGateway paymentGateway;



    @Transactional
    public CheckoutResponseDto placeOrder(UUID cartId)  {

        var cart = cartRepository.fetchCartWithItems(cartId).orElseThrow(CartNotFoundException::new);
        if (cart.isEmpty()) throw new CartEmptyException();
        var order= OrderEntity.createOrder(cart, authService.getCurrentUser());
        orderRepository.save(order);

        //create a session
        try {
           var checkoutsession= paymentGateway.createCheckoutSession(order);
            cartService.clearCart(cartId);
            return new CheckoutResponseDto(order.getOrderId(),checkoutsession.getCheckoutUrl());
        } catch (PaymentException e) {
            orderRepository.delete(order);
            throw e;
        }
    }

    public void handleWebhookEvent(WebhookEventRequest request){
       paymentGateway.parseWebhookRequest(request)
               .ifPresent(pr-> {
                           var order = orderRepository.findById(pr.getOrderId()).orElseThrow();
                           order.setPaymentStatus(pr.getPaymentStatus());
                           orderRepository.save(order);
                       }
                       );
    }

}
