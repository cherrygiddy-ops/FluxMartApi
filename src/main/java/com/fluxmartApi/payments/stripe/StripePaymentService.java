package com.fluxmartApi.payments.stripe;

import com.fluxmartApi.payments.WebhookEventRequest;
import com.fluxmartApi.order.OrderEntity;
import com.fluxmartApi.order.OrderItemsEntity;
import com.fluxmartApi.order.PaymentStatus;
import com.fluxmartApi.payments.CheckoutSession;
import com.fluxmartApi.payments.PaymentException;
import com.fluxmartApi.payments.PaymentGateway;
import com.fluxmartApi.payments.PaymentResults;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.net.ApiResource;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class StripePaymentService implements PaymentGateway {
    private final StripeConfig stripeConfig;
    @Value("${websiteUrl}")
    private String websiteUrl;

    @Override
    public CheckoutSession createCheckoutSession(OrderEntity order)  {
        try {
            var builder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(websiteUrl +"/checkout-success?orderId="+order.getOrderId())
                    .setCancelUrl(websiteUrl + "/checkout-cancel")
                    .putMetadata("orderId",order.getOrderId().toString());
            order.getOrderItems().forEach(item->{
                var lineItem = createLineItem(item);
                builder.addLineItem(lineItem);
            });
            var session= Session.create(builder.build());
            return new CheckoutSession(session.getUrl());
    }catch (StripeException ex){
            System.out.println(ex.getMessage());
            throw new PaymentException("Can not create Session");
        }
}

    @Override
    public Optional<PaymentResults> parseWebhookRequest(WebhookEventRequest request) {
        var payload = request.getPayload();
        var signature = request.getHeaderSignature().get("stripe-signature");
        try {
            var event= Webhook.constructEvent(payload,signature,stripeConfig.getWebhooksecretkey());

            switch (event.getType()) {
                case "payment_intent.succeeded" -> {
                        return Optional.of(new PaymentResults(extractOrderIdFromPaymentIntent(payload), PaymentStatus.PAID));
                }
                case "payment_intent.payment_failed" ->{
                    return Optional.of(new PaymentResults(extractOrderIdFromPaymentIntent(payload), PaymentStatus.FAILED));
                }
                default -> {
                   return Optional.empty();
                }
            }
        }catch (StripeException ex){
            System.out.println( ex.getMessage());//logging the exception in a logger service
            throw new PaymentException("invalid Signature");
        }
    }

    public static Integer extractOrderIdFromPaymentIntent(String payload) {
        JsonObject root = JsonParser.parseString(payload).getAsJsonObject();
        JsonObject dataObject = root.getAsJsonObject("data").getAsJsonObject("object");

        PaymentIntent intent = ApiResource.GSON.fromJson(dataObject, PaymentIntent.class);
        System.out.println(intent);
        return  Integer.valueOf(intent.getMetadata().get("orderId"));
    }

    private static SessionCreateParams.LineItem createLineItem(OrderItemsEntity item) {
        return SessionCreateParams.LineItem.builder()
                .setQuantity(Long.valueOf(item.getQuantity()))
                .setPriceData(createPriceData(item)
                ).build();
    }

    private static SessionCreateParams.LineItem.PriceData createPriceData(OrderItemsEntity item) {
        return SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency("kes")
                .setUnitAmountDecimal(item.getUnitPrice().multiply(BigDecimal.valueOf(1000)))
                .setProductData(createProductData(item)).build();
    }

    private static SessionCreateParams.LineItem.PriceData.ProductData createProductData(OrderItemsEntity item) {
        return SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName(item.getProduct().getName()).build();
    }
}
