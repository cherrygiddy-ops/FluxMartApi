package com.fluxmartApi.payments.mpesa.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fluxmartApi.payments.mpesa.config.MpesaConfiguration;
import com.fluxmartApi.payments.mpesa.dtos.*;
import com.fluxmartApi.payments.mpesa.entities.B2C_C2B_Entries;
import com.fluxmartApi.payments.mpesa.repository.B2CC2BEntriesRepository;
import com.fluxmartApi.payments.mpesa.repository.StkPushEntriesRepository;
import com.fluxmartApi.payments.mpesa.utils.HelperUtility;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import static com.fluxmartApi.payments.mpesa.utils.Constants.*;
import static com.fluxmartApi.payments.mpesa.utils.Constants.AUTHORIZATION_HEADER_STRING;
import static com.fluxmartApi.payments.mpesa.utils.Constants.BEARER_AUTH_STRING;
import static com.fluxmartApi.payments.mpesa.utils.Constants.JSON_MEDIA_TYPE;
import static com.fluxmartApi.payments.mpesa.utils.Constants.TRANSACTION_STATUS_VALUE;

@Service
@Slf4j
@AllArgsConstructor
public class B2CImpl {
    private final CommonServices commonServices;
    private final MpesaConfiguration mpesaConfiguration;
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;
    private final B2CC2BEntriesRepository b2CC2BEntriesRepository;
    private final AcknowledgeResponse acknowledgeResponse;
    public TransactionStatusSyncResponse getB2CTransactionResult(InternalTransactionStatusRequest internalTransactionStatusRequest) {

        TransactionStatusRequest transactionStatusRequest = new TransactionStatusRequest();
        transactionStatusRequest.setTransactionID(internalTransactionStatusRequest.getBillRefNumber());

        transactionStatusRequest.setInitiator(mpesaConfiguration.getB2cInitiatorName());
        transactionStatusRequest.setSecurityCredential(mpesaConfiguration.getSecurityCredentials());
        transactionStatusRequest.setCommandID(TRANSACTION_STATUS_QUERY_COMMAND);
        transactionStatusRequest.setPartyA(mpesaConfiguration.getShortCode());
        transactionStatusRequest.setIdentifierType(SHORT_CODE_IDENTIFIER);
        transactionStatusRequest.setResultURL(mpesaConfiguration.getB2cResultUrl());
        transactionStatusRequest.setQueueTimeOutURL(mpesaConfiguration.getB2cQueueTimeoutUrl());
        transactionStatusRequest.setRemarks(TRANSACTION_STATUS_VALUE);
        transactionStatusRequest.setOccasion(TRANSACTION_STATUS_VALUE);

        AccessTokenResponse accessTokenResponse = commonServices.getAccessToken();

        RequestBody body = RequestBody.create(Objects.requireNonNull(HelperUtility.toJson(transactionStatusRequest)),
                JSON_MEDIA_TYPE);

        Request request = new Request.Builder()
                .url(mpesaConfiguration.getTransactionResultUrl())
                .post(body)
                .addHeader(AUTHORIZATION_HEADER_STRING, String.format("%s %s", BEARER_AUTH_STRING, accessTokenResponse.getAccessToken()))
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return null;
            }

            assert response.body() != null;

            String responseBodyString = Objects.requireNonNull(response.body()).string();
            return objectMapper.readValue(responseBodyString, TransactionStatusSyncResponse.class);
        } catch (IOException e) {
            return null;
        }

    }


    public CommonSyncResponse performB2CTransaction(InternalB2CTransactionRequest internalRequest) {
        AccessTokenResponse token = commonServices. getAccessToken();

        B2CTransactionRequest requestPayload = new B2CTransactionRequest();
        requestPayload.setInitiatorName(mpesaConfiguration.getB2cInitiatorName());
        requestPayload.setSecurityCredential(mpesaConfiguration.getSecurityCredentials());
        requestPayload.setCommandID(internalRequest.getCommandID());
        requestPayload.setAmount(String.valueOf(internalRequest.getAmount()));
        requestPayload.setPartyA(mpesaConfiguration.getShortCode());
        requestPayload.setPartyB(internalRequest.getPartyB());
        requestPayload.setRemarks(internalRequest.getRemarks());
        requestPayload.setQueueTimeOutURL(mpesaConfiguration.getB2cQueueTimeoutUrl());
        requestPayload.setResultURL(mpesaConfiguration.getB2cResultUrl());

        RequestBody body = RequestBody.create(
                HelperUtility.toJson(requestPayload),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(mpesaConfiguration.getB2cTransactionEndpoint())
                .post(body)
                .addHeader("Authorization", "Bearer " + token.getAccessToken())
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            System.out.println(response);
            if (!response.isSuccessful()) {
                System.err.println("B2C failed: " + response.code());
                return null;
            }

            String responseBody = response.body().string();
            return objectMapper.readValue(responseBody, CommonSyncResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



    public CommonSyncResponse performB2CTransactionAndPersist(InternalB2CTransactionRequest internalB2CTransactionRequest) {
        CommonSyncResponse commonSyncResponse = performB2CTransaction(internalB2CTransactionRequest);

        if (commonSyncResponse != null) {
            B2C_C2B_Entries b2C_c2BEntry = new B2C_C2B_Entries();
            b2C_c2BEntry.setTransactionType("B2C");
            b2C_c2BEntry.setAmount(internalB2CTransactionRequest.getAmount());
            b2C_c2BEntry.setEntryDate(new Date());
            b2C_c2BEntry.setOriginatorConversationId(commonSyncResponse.getOriginatorConversationID());
            b2C_c2BEntry.setConversationId(commonSyncResponse.getConversationID());
            b2C_c2BEntry.setMsisdn(internalB2CTransactionRequest.getPartyB());

            b2CC2BEntriesRepository.save(b2C_c2BEntry);
        }
        return commonSyncResponse;
    }

    public AcknowledgeResponse handleB2CTransactionAsyncResults(B2CTransactionAsyncResponse b2CTransactionAsyncResponse) {
        try {
            log.info(objectMapper.writeValueAsString(b2CTransactionAsyncResponse));
        } catch (JsonProcessingException e) {
            log.error("Error logging B2CTransactionAsyncResponse: {}", e.getMessage());
        }

        Result b2cResult = b2CTransactionAsyncResponse.getResult();

        Optional<B2C_C2B_Entries> optionalB2cInternalRecord = b2CC2BEntriesRepository.findByConversationIdOrOriginatorConversationId(
                b2cResult.getConversationID(),
                b2cResult.getOriginatorConversationID());

        if (optionalB2cInternalRecord.isPresent()) {
            B2C_C2B_Entries b2cInternalRecord = optionalB2cInternalRecord.get();
            b2cInternalRecord.setRawCallbackPayloadResponse(b2CTransactionAsyncResponse);
            b2cInternalRecord.setResultCode(String.valueOf(b2cResult.getResultCode()));
            b2cInternalRecord.setTransactionId(b2cResult.getTransactionID());

            b2CC2BEntriesRepository.save(b2cInternalRecord);
        } else {
            log.error("B2C_C2B_Entry not found for ConversationID: {} or OriginatorConversationID: {}", b2cResult.getConversationID(), b2cResult.getOriginatorConversationID());
        }
        return acknowledgeResponse;
    }
}
