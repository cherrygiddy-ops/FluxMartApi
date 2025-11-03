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
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
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
        var payload = request.getPayload();
        var signature = request.getHeaderSignature().get("stripe-signature");
        try {
            var event= Webhook.constructEvent(payload,signature,stripeConfig.getWebhooksecretkey());

            switch (event.getType()) {
                case "payment_intent.succeeded" -> {
                   var payloadme= extractMetadataFromPaymentIntent(payload);

                    var transaction = new TransactionEntity();
                    transaction.setTransactionDate(new Date());
                    transaction.setTransactionId(payloadme.id);
                    transaction.setOrderId(Integer.valueOf(payloadme.orderId));
                    transaction.setPaymentType("STRIPE");
                    transaction.setAmount(payloadme.amountPaid);

                    // Get total across all records
                    BigDecimal existingTotal = transactionsRepository.findTotalAmountAcrossAllTransactions();
                    transaction.setTotalAmount(existingTotal.add(payloadme.amountPaid));

                    transactionsRepository.save(transaction);


                    return Optional.of(new PaymentResults(Integer.valueOf(payloadme.orderId), PaymentStatus.PAID));

                }
                case "payment_intent.payment_failed" ->{
                    return Optional.of(new PaymentResults(Integer.valueOf(extractMetadataFromPaymentIntent(payload).orderId), PaymentStatus.FAILED));
                }
                case "payment_intent.canceled" ->{
                    return Optional.of(new PaymentResults(Integer.valueOf(extractMetadataFromPaymentIntent(payload).orderId), PaymentStatus.CANCELLED));
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

    @Override
    public Optional<PaymentResults> confirmStkPushAndUpdateOrder() {
        return Optional.empty();
    }

    @Override
    public Optional<PaymentResults> confirmC2bTransactionAndUpdateOrder(InternalTransactionStatusRequest request) {
        return Optional.empty();
    }

    public static PayloadMetaData extractMetadataFromPaymentIntent(String payload) {
        JsonObject root = JsonParser.parseString(payload).getAsJsonObject();
        JsonObject dataObject = root.getAsJsonObject("data").getAsJsonObject("object");

// Deserialize into PaymentIntent
        PaymentIntent intent = ApiResource.GSON.fromJson(dataObject, PaymentIntent.class);

// Extract values
        String transactionId = intent.getId(); // Stripe's internal transaction ID
        String orderId = intent.getMetadata().get("orderId"); // Your custom metadata
        Long amountInCents = intent.getAmount(); // Amount in smallest currency unit

// Convert amount to BigDecimal in KES
        BigDecimal amount = BigDecimal.valueOf(amountInCents).divide(BigDecimal.valueOf(100));

        return  new PayloadMetaData(transactionId,amount,orderId);
    }


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
