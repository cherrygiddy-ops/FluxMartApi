package com.fluxmartApi.cart;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class AddToCartRequest {

    @JsonProperty("productId")
    private Integer productId;
}
