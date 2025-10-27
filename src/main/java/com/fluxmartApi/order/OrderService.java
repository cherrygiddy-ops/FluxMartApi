package com.fluxmartApi.order;

import com.fluxmartApi.auth.AuthService;
import com.fluxmartApi.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final AuthService authService;
    private final OrderMapper orderMapper;

    public OrderResponseDto getOrderDetailsForCustomer(Integer orderId) {
        var orders= getAllOrderForCustomer();
        return orders.stream().filter(o->o.getOrderId().equals(orderId)).findFirst().orElseThrow(OrderNotFoundException::new);
    }
    public List<OrderResponseDto> getAllOrderForCustomer() {
        var orders= orderRepository.loadAllOrderForCustomerWithItems(authService.getCurrentUser());
        return orders.stream().map(orderMapper::toDto).toList();
    }
}
