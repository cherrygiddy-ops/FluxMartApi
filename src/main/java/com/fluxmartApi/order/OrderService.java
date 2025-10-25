package com.fluxmartApi.order;

import com.fluxmartApi.auth.AuthService;
import com.fluxmartApi.cart.CartNotFoundException;
import com.fluxmartApi.cart.CartRepository;
import com.fluxmartApi.cart.CartService;
import com.fluxmartApi.checkout.CheckoutResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final AuthService authService;
    private final OrderMapper orderMapper;
    private final CartService cartService;

    public CheckoutResponseDto placeOrder(UUID cartId) {

        var cart = cartRepository.fetchCartWithItems(cartId).orElseThrow(CartNotFoundException::new);
        if (cart.isEmpty())
            throw new CartEmptyException();

        var order = new OrderEntity();
        order.setComments("order 1");
        order.setTotalPrice(cart.getTotalPrice());
        order.setCustomer(authService.getCurrentUser());
        order.setStatus(Status.PENDING);

        cart.getItems().stream().forEach(cartI -> {
                    var orderItem = new OrderItemsEntity();
                    orderItem.setQuantity(cartI.getQuantity());
                    orderItem.setProduct(cartI.getProduct());
                    orderItem.setTotalPrice(cartI.getTotalPrice());
                    orderItem.setUnitPrice(cartI.getProduct().getPrice());
                    orderItem.setOrder(order);

                    order.addItems(orderItem);
                }
        );
        orderRepository.save(order);
        cartService.clearCart(cartId);
        return new CheckoutResponseDto(order.getOrderId());
    }

    public OrderResponseDto getOrderDetailsForCustomer(Integer orderId) {
        var orders= getAllOrderForCustomer();
        return orders.stream().filter(o->o.getOrderId().equals(orderId)).findFirst().orElseThrow(OrderNotFoundException::new);
    }
    public List<OrderResponseDto> getAllOrderForCustomer() {
        var orders= orderRepository.loadAllOrderForCustomerWithItems(authService.getCurrentUser());
        return orders.stream().map(orderMapper::toDto).toList();
    }
}
