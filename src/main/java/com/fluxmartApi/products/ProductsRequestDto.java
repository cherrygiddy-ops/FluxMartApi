package com.fluxmartApi.products;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductsRequestDto {
    private Integer id;
    private String name;
    private String descriptions;
    private Integer quantity;
    private BigDecimal price;
    private Byte categoryId;

}
