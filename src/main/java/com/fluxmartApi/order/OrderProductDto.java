package com.fluxmartApi.order;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderProductDto {
    private Integer id;
    private String name;
    private BigDecimal price;
}
