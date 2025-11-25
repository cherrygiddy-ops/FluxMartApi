package com.fluxmartApi.payments;

import com.fluxmartApi.auth.AuthService;
import com.fluxmartApi.cart.CartNotFoundException;
import com.fluxmartApi.cart.CartRepository;
import com.fluxmartApi.cart.CartService;
import com.fluxmartApi.order.*;
import com.fluxmartApi.payments.mpesa.dtos.InternalTransactionStatusRequest;
import com.fluxmartApi.payments.stripe.CheckoutResponseDto;
import com.fluxmartApi.payments.transactions.TransactionsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;


@RequiredArgsConstructor
@Service
public class CheckOutService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final AuthService authService;
    private final CartService cartService;
    private final PaymentGateway paymentGateway;

    @Autowired
    private final Map<String, PaymentGateway> paymentGateways;

    @Transactional
    public CheckoutResponseDto placeOrder(UUID cartId,String gateway)  {

        var cart = cartRepository.fetchCartWithItems(cartId).orElseThrow(CartNotFoundException::new);
        if (cart.isEmpty()) throw new CartEmptyException();
        var order= OrderEntity.createOrder(cart, authService.getCurrentUser());
        orderRepository.save(order);

        PaymentGateway selectedGateway = paymentGateways.get(gateway + "PaymentService");
        if (selectedGateway == null) {
            throw new IllegalArgumentException("Unsupported payment gateway: " + gateway);
        }

        try {
            var checkoutSession = selectedGateway.createCheckoutSession(order);
            cartService.clearCart(cartId);
            return new CheckoutResponseDto(order.getOrderId(), checkoutSession.getCheckoutUrl());
        } catch (PaymentException e) {
            orderRepository.delete(order);
            throw e;
        }

    }

    public void handleWebhookEvent(WebhookEventRequest request) {
        System.out.println("Handling webhook event...");
        paymentGateway.parseWebhookRequest(request)
                .ifPresent(pr -> {
                    System.out.println("Updating order {} to status {}"+pr.getOrderId()+pr.getPaymentStatus());
                    var order = orderRepository.findById(pr.getOrderId()).orElseThrow();
                    order.setPaymentStatus(pr.getPaymentStatus());
                    orderRepository.save(order);
                });
    }
    public void confirmStkPushAndUpdateOrder(){
        paymentGateway.confirmStkPushAndUpdateOrder()
                .ifPresent(pr-> {
                            var order = orderRepository.findById(pr.getOrderId()).orElseThrow();
                            if (order.getPaymentStatus()==PaymentStatus.PAID)
                                throw new OrderAlreadyUpdatedException();
                            order.setPaymentStatus(pr.getPaymentStatus());
                            orderRepository.save(order);
                        }
                );
    }

    public void confirmC2BTransactionUpdateOrder(InternalTransactionStatusRequest request){
        paymentGateway.confirmC2bTransactionAndUpdateOrder(request)
                .ifPresentOrElse(pr-> {
                            var order = orderRepository.findById(pr.getOrderId()).orElseThrow();
                            if (order.getPaymentStatus()==PaymentStatus.PAID)
                                throw new OrderAlreadyUpdatedException();
                            order.setPaymentStatus(pr.getPaymentStatus());
                            orderRepository.save(order);
                        },  () -> {
                            // Handle the case when the Optional is empty
                            throw new OrderNotFoundException(); // or log, or do nothing
                        }

                );
    }

}
