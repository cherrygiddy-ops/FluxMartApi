package com.fluxmartApi.payments;

import com.fluxmartApi.order.OrderEntity;
import com.fluxmartApi.payments.mpesa.dtos.InternalTransactionStatusRequest;

import java.util.Optional;

public interface PaymentGateway {
    CheckoutSession createCheckoutSession(OrderEntity ordert);

    Optional<PaymentResults> parseWebhookRequest(WebhookEventRequest request);

    void  postTransactions();

    Optional<PaymentResults> updateOrderAndPostTransactions(InternalTransactionStatusRequest request);
}
