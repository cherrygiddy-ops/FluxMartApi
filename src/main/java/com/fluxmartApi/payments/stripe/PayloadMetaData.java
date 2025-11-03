package com.fluxmartApi.payments.stripe;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Setter
public class PayloadMetaData {

    String id;
    BigDecimal amountPaid ;
    String orderId;

}
