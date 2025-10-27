package com.fluxmartApi.checkout;

import com.fluxmartApi.auth.AuthService;
import com.fluxmartApi.cart.CartNotFoundException;
import com.fluxmartApi.cart.CartRepository;
import com.fluxmartApi.cart.CartService;
import com.fluxmartApi.order.CartEmptyException;
import com.fluxmartApi.order.OrderEntity;
import com.fluxmartApi.order.OrderMapper;
import com.fluxmartApi.order.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@AllArgsConstructor
@Service
public class CheckOutService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final AuthService authService;
    private final CartService cartService;

    public CheckoutResponseDto placeOrder(UUID cartId) {

        var cart = cartRepository.fetchCartWithItems(cartId).orElseThrow(CartNotFoundException::new);
        if (cart.isEmpty()) throw new CartEmptyException();
        var order= OrderEntity.createOrder(cart, authService.getCurrentUser());
        orderRepository.save(order);
        cartService.clearCart(cartId);
        return new CheckoutResponseDto(order.getOrderId());
    }
}
