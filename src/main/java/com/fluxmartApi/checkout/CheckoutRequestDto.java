package com.fluxmartApi.checkout;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class CheckoutRequestDto {
    @NotBlank(message = "cartID Required")
    private UUID cartId;
}
