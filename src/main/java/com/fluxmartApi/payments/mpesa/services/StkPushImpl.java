package com.fluxmartApi.payments.mpesa.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fluxmartApi.order.OrderEntity;
import com.fluxmartApi.order.OrderNotFoundException;
import com.fluxmartApi.order.OrderRepository;
import com.fluxmartApi.order.PaymentStatus;
import com.fluxmartApi.payments.*;
import com.fluxmartApi.payments.mpesa.config.MpesaConfiguration;
import com.fluxmartApi.payments.mpesa.dtos.*;
import com.fluxmartApi.payments.mpesa.entities.StkPush_Entries;
import com.fluxmartApi.payments.mpesa.repository.B2CC2BEntriesRepository;
import com.fluxmartApi.payments.mpesa.repository.StkPushEntriesRepository;
import com.fluxmartApi.payments.mpesa.utils.Constants;
import com.fluxmartApi.payments.mpesa.utils.HelperUtility;
import com.fluxmartApi.payments.transactions.TransactionEntity;
import com.fluxmartApi.payments.transactions.TransactionsRepository;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.fluxmartApi.payments.mpesa.utils.Constants.*;

@Primary
@Service("STKPushPaymentService")
@RequiredArgsConstructor
public class StkPushImpl implements PaymentGateway {

    private final MpesaConfiguration mpesaConfiguration;
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;
    private final StkPushEntriesRepository stkPushEntriesRepository;
    private  final OrderRepository orderRepository;
   private  final TransactionsRepository transactionsRepository;
   private  final CommonServices commonServices;
    private BigDecimal totalPriceFromOrder;
    private String orderNumber;



    public StkPushSyncResponse performStkPushTransaction(InternalStkPushRequest internalStkPushRequest) {

        ExternalStkPushRequest externalStkPushRequest = new ExternalStkPushRequest();
        externalStkPushRequest.setBusinessShortCode(mpesaConfiguration.getStkPushShortCode());

        String transactionTimestamp = HelperUtility.getTransactionTimestamp();
        String stkPushPassword = HelperUtility.getStkPushPassword(mpesaConfiguration.getStkPushShortCode(),
                mpesaConfiguration.getStkPassKey(), transactionTimestamp);

        externalStkPushRequest.setPassword(stkPushPassword);
        externalStkPushRequest.setTimestamp(transactionTimestamp);
        externalStkPushRequest.setTransactionType(Constants.CUSTOMER_PAYBILL_ONLINE);
        externalStkPushRequest.setAmount(totalPriceFromOrder);
        externalStkPushRequest.setPartyA(internalStkPushRequest.getPhoneNumber());
        externalStkPushRequest.setPartyB(mpesaConfiguration.getStkPushShortCode());
        externalStkPushRequest.setPhoneNumber(internalStkPushRequest.getPhoneNumber());
        externalStkPushRequest.setCallBackURL(mpesaConfiguration.getStkPushRequestCallbackUrl());
        externalStkPushRequest.setAccountReference(orderNumber);
        externalStkPushRequest.setTransactionDesc(String.format("%s Make an Order", internalStkPushRequest.getPhoneNumber()));

        AccessTokenResponse accessTokenResponse = commonServices.getAccessToken();

        RequestBody body = RequestBody.create(Objects.requireNonNull(HelperUtility.toJson(externalStkPushRequest)),
                JSON_MEDIA_TYPE);

        Request request = new Request.Builder()
                .url(mpesaConfiguration.getStkPushRequestUrl())
                .post(body)
                .addHeader(AUTHORIZATION_HEADER_STRING, String.format("%s %s", BEARER_AUTH_STRING, accessTokenResponse.getAccessToken()))
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return null;
            }

            assert response.body() != null;

            String responseBodyString = Objects.requireNonNull(response.body()).string();
            return objectMapper.readValue(responseBodyString, StkPushSyncResponse.class);
        } catch (IOException e) {
            return null;
        }

    }

    public void acknowledgeStkPushResponse(StkPushAsyncResponse callbackRequest) {

        Logger log = LoggerFactory.getLogger(this.getClass());

        StkCallback callback = callbackRequest.getBody().getStkCallback();

        if (callback.getResultCode() == 0) {

            Map<String, Object> metadata = new HashMap<>();
            if (callback.getCallbackMetadata() != null && callback.getCallbackMetadata().getItem() != null) {
                for (ItemItem item : callback.getCallbackMetadata().getItem()) {
                    metadata.put(item.getName(), item.getValue());
                }
            }

            StkPush_Entries stkPushEntry = new StkPush_Entries();
            stkPushEntry.setTransactionType("Stk Push Was Successful.");
            stkPushEntry.setAmount(new BigDecimal(metadata.get("Amount").toString()));

            stkPushEntry.setMsisdn(metadata.get("PhoneNumber").toString());
            stkPushEntry.setMerchantRequestID(callback.getMerchantRequestID());
            stkPushEntry.setCheckoutRequestID(callback.getCheckoutRequestID());

            if (metadata.get("MpesaReceiptNumber") != null) {
                stkPushEntry.setMpesaReceiptNumber(metadata.get("MpesaReceiptNumber").toString());
            }

            if (metadata.get("TransactionDate") != null) {
                try {
                    String transactionDateString = metadata.get("TransactionDate").toString();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                    Date transactionDate = dateFormat.parse(transactionDateString);
                    stkPushEntry.setEntryDate(transactionDate);
                } catch (Exception e) {
                    log.error("Failed to parse TransactionDate: " + metadata.get("TransactionDate"), e);
                    stkPushEntry.setEntryDate(new Date());
                }
            } else {
                stkPushEntry.setEntryDate(new Date());
            }

            stkPushEntry.setResultCode(String.valueOf(callback.getResultCode()));
            stkPushEntry.setResultDesc(orderNumber);

            stkPushEntriesRepository.save(stkPushEntry);

            log.info("Successfully processed STK callback for CheckoutRequestID: {}", callback.getCheckoutRequestID());
        } else {
            log.info("STK transaction failed - CheckoutRequestID: {}, ResultCode: {}, ResultDesc: {}",
                    callback.getCheckoutRequestID(), callback.getResultCode(), callback.getResultDesc());
        }
    }
    @Override
    public CheckoutSession createCheckoutSession(OrderEntity order) {
        totalPriceFromOrder = order.getTotalPrice();
        orderNumber = String.valueOf(order.getOrderId());
      return new CheckoutSession("");
    }

    @Override
    public Optional<PaymentResults> parseWebhookRequest(WebhookEventRequest request) {
       return Optional.empty();
    }

    @Override
    public Optional<PaymentResults> confirmStkPushAndUpdateOrder() {
        var stktransaction= stkPushEntriesRepository.findByResultDesc(orderNumber).orElseThrow(TransactionNotFound::new);
        BigDecimal existingTotal = transactionsRepository.findTotalAmountAcrossAllTransactions();
        var orderId = stktransaction.getResultDesc();
        var order = orderRepository.findById(Integer.valueOf(orderId)).orElseThrow(OrderNotFoundException::new);
        if (order.getPaymentStatus()==PaymentStatus.PAID)
            throw new OrderAlreadyUpdatedException();
        var transaction = new TransactionEntity();
        transaction.setTransactionDate(stktransaction.getEntryDate());
        transaction.setTransactionId(stktransaction.getMpesaReceiptNumber());
        transaction.setOrderId(Integer.valueOf(stktransaction.getResultDesc()));
        transaction.setPaymentType(stktransaction.getTransactionType());
        transaction.setAmount(stktransaction.getAmount());
        transaction.setTotalAmount(existingTotal.add(stktransaction.getAmount()));
        transactionsRepository.save(transaction);

        return Optional.of(new PaymentResults(Integer.valueOf(orderId),PaymentStatus.PAID));

    }

    @Override
    public Optional<PaymentResults> confirmC2bTransactionAndUpdateOrder(InternalTransactionStatusRequest request) {
        return Optional.empty();
    }

    public LNMQueryResponse getTransactionStatus(InternalLNMRequest internalLNMRequest) {

        ExternalLNMQueryRequest externalLNMQueryRequest = new ExternalLNMQueryRequest();
        externalLNMQueryRequest.setBusinessShortCode(mpesaConfiguration.getStkPushShortCode());

        String requestTimestamp = HelperUtility.getTransactionTimestamp();
        String stkPushPassword = HelperUtility.getStkPushPassword(mpesaConfiguration.getStkPushShortCode(),
                mpesaConfiguration.getStkPassKey(), requestTimestamp);

        externalLNMQueryRequest.setPassword(stkPushPassword);
        externalLNMQueryRequest.setTimestamp(requestTimestamp);
        externalLNMQueryRequest.setCheckoutRequestID(internalLNMRequest.getCheckoutRequestID());

        AccessTokenResponse accessTokenResponse = commonServices.getAccessToken();

        RequestBody body = RequestBody.create( Objects.requireNonNull(HelperUtility.toJson(externalLNMQueryRequest)),
                JSON_MEDIA_TYPE);

        Request request = new Request.Builder()
                .url(mpesaConfiguration.getLnmQueryRequestUrl())
                .post(body)
                .addHeader(AUTHORIZATION_HEADER_STRING, String.format("%s %s", BEARER_AUTH_STRING, accessTokenResponse.getAccessToken()))
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return null;
            }

            assert response.body() != null;
            // Use Jackson to Deserialize the ResponseBody ...
            String responseBodyString = Objects.requireNonNull(response.body()).string();
            return objectMapper.readValue(responseBodyString, LNMQueryResponse.class);
        } catch (IOException e) {
            return null;
        }

    }
}
