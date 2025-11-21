package com.fluxmartApi.cart;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class CartProductDto {
    private Integer id;
    private String name;
    private BigDecimal price=BigDecimal.ZERO;
    private String imageUrl;
}
