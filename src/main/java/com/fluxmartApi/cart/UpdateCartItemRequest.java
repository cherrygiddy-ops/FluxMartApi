package com.fluxmartApi.cart;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCartItemRequest {
    @NotNull
    private Integer quantity;
}
