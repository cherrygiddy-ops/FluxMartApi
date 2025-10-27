package com.fluxmartApi.payments;

import com.fluxmartApi.order.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentResults {
    private Integer orderId;
    private PaymentStatus paymentStatus;
}
