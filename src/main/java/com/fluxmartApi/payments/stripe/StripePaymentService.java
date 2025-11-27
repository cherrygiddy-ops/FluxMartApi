package com.fluxmartApi.payments.stripe;

import com.fluxmartApi.payments.WebhookEventRequest;
import com.fluxmartApi.order.OrderEntity;
import com.fluxmartApi.order.OrderItemsEntity;
import com.fluxmartApi.order.PaymentStatus;
import com.fluxmartApi.payments.CheckoutSession;
import com.fluxmartApi.payments.PaymentException;
import com.fluxmartApi.payments.PaymentGateway;
import com.fluxmartApi.payments.PaymentResults;
import com.fluxmartApi.payments.mpesa.dtos.InternalTransactionStatusRequest;
import com.fluxmartApi.payments.transactions.TransactionEntity;
import com.fluxmartApi.payments.transactions.TransactionsRepository;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.ApiResource;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

@RequiredArgsConstructor
@Service("StripePaymentService")
public class StripePaymentService implements PaymentGateway {
    private final StripeConfig stripeConfig;
    private final TransactionsRepository transactionsRepository;
    @Value("${websiteUrl}")
    private String websiteUrl;

    @Override
    public CheckoutSession createCheckoutSession(OrderEntity order)  {
        try {
            var builder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(websiteUrl +"/checkout-success?orderId="+order.getOrderId())
                    .setCancelUrl(websiteUrl + "/checkout-cancel?orderId="+order.getOrderId())
                    .setPaymentIntentData(createPaymentIntentData(order));
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
        String payload = request.getPayload();
        String sigHeader = request.getHeaderSignature().get("stripe-signature");
        Event event;

        try {
            // Step 1: Basic JSON deserialization
            event = ApiResource.GSON.fromJson(payload, Event.class);
        } catch (JsonSyntaxException e) {
            // Invalid payload
            throw new PaymentException("Invalid JSON payload");
        }

        // Step 2: Verify signature if secret is defined
        try {
            event = Webhook.constructEvent(payload, sigHeader, stripeConfig.getWebhooksecretkey());
        } catch (SignatureVerificationException e) {
            throw new PaymentException("Invalid Stripe signature");
        }

        // Step 3: Safely extract nested object
        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = deserializer.getObject().orElse(null);

        if (stripeObject == null) {
            // Couldn’t deserialize object (API version mismatch, etc.)
            return Optional.empty();
        }

        // Step 4: Handle event types
        switch (event.getType()) {
            case "payment_intent.succeeded" -> {
                PaymentIntent intent = (PaymentIntent) stripeObject;
                var payloadMeta = extractMetadataFromPaymentIntent(intent);

                var transaction = new TransactionEntity();
                transaction.setTransactionDate(new Date());
                transaction.setTransactionId(payloadMeta.id);
                transaction.setOrderId(Integer.valueOf(payloadMeta.orderId));
                transaction.setPaymentType("STRIPE");
                transaction.setAmount(payloadMeta.amountPaid);

                BigDecimal existingTotal = transactionsRepository.findTotalAmountAcrossAllTransactions();
                transaction.setTotalAmount(existingTotal.add(payloadMeta.amountPaid));

                transactionsRepository.save(transaction);

                return Optional.of(new PaymentResults(Integer.valueOf(payloadMeta.orderId), PaymentStatus.PAID));
            }
            case "payment_intent.payment_failed" -> {
                PaymentIntent intent = (PaymentIntent) stripeObject;
                var payloadMeta = extractMetadataFromPaymentIntent(intent);
                return Optional.of(new PaymentResults(Integer.valueOf(payloadMeta.orderId), PaymentStatus.FAILED));
            }
            case "payment_intent.canceled" -> {
                PaymentIntent intent = (PaymentIntent) stripeObject;
                var payloadMeta = extractMetadataFromPaymentIntent(intent);
                return Optional.of(new PaymentResults(Integer.valueOf(payloadMeta.orderId), PaymentStatus.CANCELLED));
            }
            default -> {
                System.out.println("Unhandled event type: " + event.getType());
                return Optional.empty();
            }
        }
    }

    @Override
    public void postTransactions() {

    }

    /**
     * Helper: safely extract PaymentIntent from Event
     */
    private PaymentIntent extractPaymentIntent(Event event) {
        var deserializer = event.getDataObjectDeserializer();
        if (deserializer.getObject().isPresent()) {
            return (PaymentIntent) deserializer.getObject().get();
        } else {
            // fallback: parse raw JSON
            String rawJson = deserializer.getRawJson();
            if (rawJson != null) {
                return ApiResource.GSON.fromJson(rawJson, PaymentIntent.class);
            }
        }
        return null;
    }

    /**
     * Refactored metadata extractor
     */
    public static PayloadMetaData extractMetadataFromPaymentIntent(PaymentIntent intent) {
        String transactionId = intent.getId();
        String orderId = intent.getMetadata().get("orderId");
        Long amountInCents = intent.getAmount();
        BigDecimal amount = BigDecimal.valueOf(amountInCents).divide(BigDecimal.valueOf(100));
        return new PayloadMetaData(transactionId, amount, orderId);
    }



    @Override
    public Optional<PaymentResults> updateOrderAndPostTransactions(InternalTransactionStatusRequest request) {
        return Optional.empty();
    }

//    public static PayloadMetaData extractMetadataFromPaymentIntent(String payload) {
//        JsonObject root = JsonParser.parseString(payload).getAsJsonObject();
//        JsonObject dataObject = root.getAsJsonObject("data").getAsJsonObject("object");
//
//// Deserialize into PaymentIntent
//        PaymentIntent intent = ApiResource.GSON.fromJson(dataObject, PaymentIntent.class);
//
//// Extract values
//        String transactionId = intent.getId(); // Stripe's internal transaction ID
//        String orderId = intent.getMetadata().get("orderId"); // Your custom metadata
//        Long amountInCents = intent.getAmount(); // Amount in smallest currency unit
//
//// Convert amount to BigDecimal in KES
//        BigDecimal amount = BigDecimal.valueOf(amountInCents).divide(BigDecimal.valueOf(100));
//
//        return  new PayloadMetaData(transactionId,amount,orderId);
//    }


    private static SessionCreateParams.PaymentIntentData createPaymentIntentData(OrderEntity order) {
        return SessionCreateParams.PaymentIntentData.builder().putMetadata("orderId", order.getOrderId().toString()).build();
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
                .setUnitAmountDecimal(item.getUnitPrice().multiply(BigDecimal.valueOf(100)))
                .setProductData(createProductData(item)).build();
    }

    private static SessionCreateParams.LineItem.PriceData.ProductData createProductData(OrderItemsEntity item) {
        return SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName(item.getProduct().getName()).build();
    }
}
