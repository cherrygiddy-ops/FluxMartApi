package com.fluxmartApi.order;

import com.fluxmartApi.auth.AuthService;
import com.fluxmartApi.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final AuthService authService;
    private final OrderMapper orderMapper;

    private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    public OrderResponseDto getOrderDetailsForCustomer(Integer orderId) {
        var orders= getAllOrderForCustomer();
        return orders.stream().filter(o->o.getOrderId().equals(orderId)).findFirst().orElseThrow(OrderNotFoundException::new);
    }
    public List<OrderResponseDto> getAllOrderForCustomer() {
        var orders= orderRepository.loadAllOrderForCustomerWithItems(authService.getCurrentUser());
        return orders.stream().map(orderMapper::toDto).toList();
    }

    public static String generateOrderCode() {
            StringBuilder sb = new StringBuilder(LENGTH);
            for (int i = 0; i < LENGTH; i++) {
                int index = RANDOM.nextInt(CHAR_POOL.length());
                sb.append(CHAR_POOL.charAt(index));
            }
            return sb.toString();
        }

}
