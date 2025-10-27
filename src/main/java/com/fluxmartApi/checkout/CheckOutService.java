package com.fluxmartApi.checkout;

import com.fluxmartApi.auth.AuthService;
import com.fluxmartApi.cart.CartNotFoundException;
import com.fluxmartApi.cart.CartRepository;
import com.fluxmartApi.cart.CartService;
import com.fluxmartApi.order.CartEmptyException;
import com.fluxmartApi.order.OrderEntity;
import com.fluxmartApi.order.OrderMapper;
import com.fluxmartApi.order.OrderRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CheckOutService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final AuthService authService;
    private final CartService cartService;

    @Value("${websiteUrl}")
    private String websiteUrl;

    @Transactional
    public CheckoutResponseDto placeOrder(UUID cartId) throws StripeException {

        var cart = cartRepository.fetchCartWithItems(cartId).orElseThrow(CartNotFoundException::new);
        if (cart.isEmpty()) throw new CartEmptyException();
        var order= OrderEntity.createOrder(cart, authService.getCurrentUser());
        orderRepository.save(order);

        //create a session
        try {
            var builder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(websiteUrl +"/checkout-success?orderId="+order.getOrderId())
                    .setCancelUrl(websiteUrl + "/checkout-cancel");
            order.getOrderItems().forEach(item->{
                var lineItem = SessionCreateParams.LineItem.builder()
                        .setQuantity(Long.valueOf(item.getQuantity()))
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("kes")
                                .setUnitAmountDecimal(item.getUnitPrice().multiply(BigDecimal.valueOf(1000)))
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName(item.getProduct().getName()).build()).build()
                        ).build();
           builder.addLineItem(lineItem);
            });

            var session=Session.create(builder.build());

            cartService.clearCart(cartId);
            return new CheckoutResponseDto(order.getOrderId(),session.getUrl());
        } catch (StripeException e) {
            orderRepository.delete(order);
            throw e;
        }
    }
}
