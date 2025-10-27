package com.fluxmartApi.payments;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CheckoutResponseDto {
    private Integer orderId;
    private String stripeCheckoutUrl;

}
