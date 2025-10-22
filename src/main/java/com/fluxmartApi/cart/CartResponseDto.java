package com.fluxmartApi.cart;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class CartResponseDto {
    private UUID id;
    private List<CartItemsDto> items = new ArrayList<>();
    private BigDecimal totalPrice = BigDecimal.ZERO;
}
