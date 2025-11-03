package com.fluxmartApi.payments.mpesa.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fluxmartApi.order.OrderEntity;
import com.fluxmartApi.order.OrderNotFoundException;
import com.fluxmartApi.order.OrderRepository;
import com.fluxmartApi.order.PaymentStatus;
import com.fluxmartApi.payments.*;
import com.fluxmartApi.payments.mpesa.config.MpesaConfiguration;
import com.fluxmartApi.payments.mpesa.dtos.*;
import com.fluxmartApi.payments.mpesa.entities.B2C_C2B_Entries;
import com.fluxmartApi.payments.mpesa.repository.B2CC2BEntriesRepository;
import com.fluxmartApi.payments.mpesa.repository.StkPushEntriesRepository;
import com.fluxmartApi.payments.mpesa.utils.HelperUtility;
import com.fluxmartApi.payments.transactions.TransactionEntity;
import com.fluxmartApi.payments.transactions.TransactionsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.fluxmartApi.payments.mpesa.utils.Constants.*;


@Slf4j
@Service("PayBillPaymentService")
@RequiredArgsConstructor
public class C2bImpl implements PaymentGateway {
    private final MpesaConfiguration mpesaConfiguration;
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;
    private final B2CC2BEntriesRepository b2CC2BEntriesRepository;
    private final StkPushEntriesRepository stkPushEntriesRepository;
    private final AcknowledgeResponse acknowledgeResponse;
    private  final OrderRepository orderRepository;
    private final TransactionsRepository transactionsRepository;
    private  final CommonServices commonServices;

    @Override
    public CheckoutSession createCheckoutSession(OrderEntity ordert) {
        return null;
    }

    @Override
    public Optional<PaymentResults> parseWebhookRequest(WebhookEventRequest request) {
        return Optional.empty();
    }

    @Override
    public Optional<PaymentResults> confirmStkPushAndUpdateOrder() {
        return Optional.empty();
    }

    @Override
    public Optional<PaymentResults> confirmC2bTransactionAndUpdateOrder(InternalTransactionStatusRequest request) {
        var transactions= b2CC2BEntriesRepository.findByBillRefNumber(request.getBillRefNumber());
        if (!transactions.isEmpty()) {
            BigDecimal existingTotal = transactionsRepository.findTotalAmountAcrossAllTransactions();
            var orderId = Integer.valueOf(transactions.get(0).getBillRefNumber());
            var order = orderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new);
            if (order.getPaymentStatus() == PaymentStatus.PAID)
                throw new OrderAlreadyUpdatedException();
            var totalAmount = transactions.stream().map(B2C_C2B_Entries::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            if (totalAmount.compareTo(order.getTotalPrice()) >= 0) {
                transactions.forEach(tr -> {
                    var transaction = new TransactionEntity();
                    transaction.setTransactionDate(tr.getEntryDate());
                    transaction.setTransactionId(tr.getTransactionId());
                    transaction.setOrderId(Integer.valueOf(tr.getBillRefNumber()));
                    transaction.setPaymentType(tr.getTransactionType());
                    transaction.setAmount(tr.getAmount());
                    transaction.setTotalAmount(existingTotal.add(tr.getAmount()));
                    transactionsRepository.save(transaction);
                });
                return Optional.of(new PaymentResults(orderId, PaymentStatus.PAID));
            } else
                throw new PaymentException("Amount is less than Total Order Amount");
        }
        return Optional.empty();
    }

    public SimulateTransactionResponse simulateC2BTransaction(SimulateTransactionRequest simulateTransactionRequest) {
        AccessTokenResponse accessTokenResponse = commonServices.getAccessToken();
        log.info("Access Token: {}", accessTokenResponse.getAccessToken());
        RequestBody body = RequestBody.create(Objects.requireNonNull(HelperUtility.toJson(simulateTransactionRequest)),
                JSON_MEDIA_TYPE);

        Request request = new Request.Builder()
                .url(mpesaConfiguration.getSimulateTransactionEndpoint())
                .post(body)
                .addHeader(AUTHORIZATION_HEADER_STRING, String.format("%s %s", BEARER_AUTH_STRING, accessTokenResponse.getAccessToken()))
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return null;
            }

            assert response.body() != null;

            String responseBodyString = Objects.requireNonNull(response.body()).string();
            return objectMapper.readValue(responseBodyString, SimulateTransactionResponse.class);
        } catch (IOException e) {
            return null;
        }

    }

    public SimulateTransactionResponse simulateC2BAndPersist(SimulateTransactionRequest simulateTransactionRequest) {
        SimulateTransactionResponse simulateTransactionResponse = simulateC2BTransaction(simulateTransactionRequest);
        if (simulateTransactionResponse != null) {
            B2C_C2B_Entries b2C_c2BEntry = new B2C_C2B_Entries();
            b2C_c2BEntry.setTransactionType("C2B");
            b2C_c2BEntry.setBillRefNumber(simulateTransactionRequest.getBillRefNumber());
            b2C_c2BEntry.setAmount(simulateTransactionRequest.getAmount());
            b2C_c2BEntry.setEntryDate(new Date());
            b2C_c2BEntry.setOriginatorConversationId(simulateTransactionResponse.getOriginatorCoversationID());
            b2C_c2BEntry.setConversationId(simulateTransactionResponse.getResponseCode());
            b2C_c2BEntry.setMsisdn(simulateTransactionRequest.getMsisdn());

            b2CC2BEntriesRepository.save(b2C_c2BEntry);
        }
        return simulateTransactionResponse;
    }

    @Transactional
    public AcknowledgeResponse completeMpesaTransanction(MpesaValidationResponse mpesaValidationResponse) {
        List<B2C_C2B_Entries> b2CC2BEntries = b2CC2BEntriesRepository.findByBillRefNumber(mpesaValidationResponse.getBillRefNumber());

        if (b2CC2BEntries != null && !b2CC2BEntries.isEmpty()) {
            // Get the last entry in the list
            B2C_C2B_Entries b2CC2BEntry = b2CC2BEntries.get(b2CC2BEntries.size() - 1);

            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(mpesaValidationResponse);
                b2CC2BEntry.setRawCallbackPayloadResponse(jsonString);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize MpesaValidationResponse", e);
            }

            b2CC2BEntry.setResultCode("0");
            b2CC2BEntry.setTransactionId(mpesaValidationResponse.getTransID());

            b2CC2BEntriesRepository.save(b2CC2BEntry);
            log.info("Updated last entry for BillRefNumber: {}", mpesaValidationResponse.getBillRefNumber());
        } else {
            log.error("B2C_C2B_Entry not found for BillRefNumber: {}", mpesaValidationResponse.getBillRefNumber());
        }

        return acknowledgeResponse;
    }
}
