package com.fluxmartApi.order;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponseDto {
    private Integer orderId;
    private String status;
    private LocalDateTime orderDate;
    private List<OrderItemsResponseDto> orderItems;
    private BigDecimal totalPrice;
}
