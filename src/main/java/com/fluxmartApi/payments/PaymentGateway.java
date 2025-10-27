package com.fluxmartApi.payments;

import com.fluxmartApi.order.OrderEntity;

import java.util.Optional;

public interface PaymentGateway {
    CheckoutSession createCheckoutSession(OrderEntity order);

    Optional<PaymentResults> parseWebhookRequest(WebhookEventRequest request);
}
