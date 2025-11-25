package com.fluxmartApi.payments.stripe;

import com.fluxmartApi.order.OrderRepository;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

@RequiredArgsConstructor
@Service("StripePaymentService")
public class StripePaymentService implements PaymentGateway {
    private static final Logger log = LoggerFactory.getLogger(StripePaymentService.class);
    private final StripeConfig stripeConfig;
    private final TransactionsRepository transactionsRepository;
    private  final OrderRepository orderRepository;
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
        var payload = request.getPayload();
        var signature = request.getHeaderSignature().get("stripe-signature");

        try {


            log.info("Constructing event with payload size={} and signature={}", payload.length(), signature);
            Event event = Webhook.constructEvent(payload, signature, stripeConfig.getWebhooksecretkey());
            log.info("Event constructed successfully: type={}", event.getType());

            EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
            Optional<StripeObject> stripeObject = deserializer.getObject();

            if (stripeObject.isEmpty()) {
                String rawJson = deserializer.getRawJson();
                log.warn("Deserializer empty, rawJson={}", rawJson);
                if (rawJson != null) {
                    if (event.getType().startsWith("payment_intent")) {
                        PaymentIntent intent = ApiResource.GSON.fromJson(rawJson, PaymentIntent.class);
                        return handlePaymentIntent(event.getType(), intent);
                    }
                    if ("checkout.session.completed".equals(event.getType())) {
                        Session session = ApiResource.GSON.fromJson(rawJson, Session.class);
                        return handleCheckoutSession(session);
                    }
                }
                return Optional.empty();
            }

            StripeObject obj = stripeObject.get();
            if (obj instanceof PaymentIntent intent) {
                return handlePaymentIntent(event.getType(), intent);
            }
            if (obj instanceof Session session) {
                return handleCheckoutSession(session);
            }

            return Optional.empty();
        } catch (SignatureVerificationException e) {
            log.error("Signature verification failed: {}", e.getMessage());
            throw new PaymentException("invalid signature");
        }
    }

    private Optional<PaymentResults> handlePaymentIntent(String eventType, PaymentIntent intent) {
        String transactionId = intent.getId();
        String orderId = intent.getMetadata().get("orderId"); // must match key used when creating PaymentIntent
        BigDecimal amount = BigDecimal.valueOf(intent.getAmount()).divide(BigDecimal.valueOf(100));

        log.info("Parsed PaymentIntent: id={}, orderId={}, amount={}", transactionId, orderId, amount);

        if (orderId == null) {
            log.warn("No orderId found in metadata. Cannot update order.");
            return Optional.empty();
        }

        switch (eventType) {
            case "payment_intent.succeeded" -> {
                TransactionEntity transaction = new TransactionEntity();
                transaction.setTransactionDate(new Date());
                transaction.setTransactionId(transactionId);
                transaction.setOrderId(Integer.valueOf(orderId));
                transaction.setPaymentType("STRIPE");
                transaction.setAmount(amount);

                BigDecimal existingTotal = transactionsRepository.findTotalAmountAcrossAllTransactions();
                transaction.setTotalAmount(existingTotal.add(amount));

                transactionsRepository.save(transaction);

                int updated = orderRepository.updatePaymentStatus(Integer.valueOf(orderId), PaymentStatus.PAID);
                log.info("Order update result: {} rows updated", updated);

                return Optional.of(new PaymentResults(Integer.valueOf(orderId), PaymentStatus.PAID));
            }
            case "payment_intent.payment_failed" ->
            { return Optional.of(new PaymentResults(Integer.valueOf(orderId), PaymentStatus.FAILED)); }
            case "payment_intent.canceled" ->
            { return Optional.of(new PaymentResults(Integer.valueOf(orderId), PaymentStatus.CANCELLED)); }
            default -> { return Optional.empty(); }
        }
    }

    private Optional<PaymentResults> handleCheckoutSession(Session session) {
        String orderId = session.getMetadata().get("orderId");
        log.info("Parsed Checkout Session: orderId={}", orderId);
        orderRepository.updatePaymentStatus(Integer.valueOf(orderId), PaymentStatus.PAID);
        return Optional.of(new PaymentResults(Integer.valueOf(orderId), PaymentStatus.PAID));
    }

    @Override
    public Optional<PaymentResults> confirmStkPushAndUpdateOrder() {
        return Optional.empty();
    }

    @Override
    public Optional<PaymentResults> confirmC2bTransactionAndUpdateOrder(InternalTransactionStatusRequest request) {
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
