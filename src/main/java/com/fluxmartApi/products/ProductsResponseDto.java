package com.fluxmartApi.products;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductsResponseDto {
    private Integer id;
    private String name;
    private String descriptions;
    private Integer quantity;
    private BigDecimal price;
    private Byte categoryId;

}
