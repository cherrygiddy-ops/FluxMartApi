package com.fluxmartApi.products;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductsResponseDto {
    private Integer id;
    private String name;
    private String descriptions;
    private Integer quantity;
    private BigDecimal price;
    private Byte categoryId;
    private List<ProductImageDto> images;


}
