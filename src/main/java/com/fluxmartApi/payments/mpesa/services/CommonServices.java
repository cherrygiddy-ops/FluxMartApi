package com.fluxmartApi.payments.mpesa.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fluxmartApi.payments.mpesa.config.MpesaConfiguration;
import com.fluxmartApi.payments.mpesa.dtos.*;
import com.fluxmartApi.payments.mpesa.repository.B2CC2BEntriesRepository;
import com.fluxmartApi.payments.mpesa.repository.StkPushEntriesRepository;
import com.fluxmartApi.payments.mpesa.utils.Constants;
import com.fluxmartApi.payments.mpesa.utils.HelperUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

import static com.fluxmartApi.payments.mpesa.utils.Constants.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommonServices {

    private final MpesaConfiguration mpesaConfiguration;
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;

    /**
     * @return Returns Daraja API Access Token Response
     */

    public AccessTokenResponse getAccessToken() {

        String encodedCredentials = HelperUtility.toBase64String(String.format("%s:%s", mpesaConfiguration.getConsumerKey(),
                mpesaConfiguration.getConsumerSecret()));

        Request request = new Request.Builder()
                .url(String.format("%s?grant_type=%s", mpesaConfiguration.getOauthEndpoint(), mpesaConfiguration.getGrantType()))
                .get()
                .addHeader(AUTHORIZATION_HEADER_STRING, String.format("%s %s", BASIC_AUTH_STRING, encodedCredentials))
                .addHeader(CACHE_CONTROL_HEADER, CACHE_CONTROL_HEADER_VALUE)
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException();
            }

            assert response.body() != null;

            String responseBodyString = Objects.requireNonNull(response.body()).string();
            return objectMapper.readValue(responseBodyString, AccessTokenResponse.class);
        } catch (IOException e) {
            return null;
        }

    }


    public RegisterUrlResponse registerUrl() {
        AccessTokenResponse accessTokenResponse = getAccessToken();

        RegisterUrlRequest registerUrlRequest = new RegisterUrlRequest();
        registerUrlRequest.setConfirmationURL(mpesaConfiguration.getConfirmationURL());
        registerUrlRequest.setResponseType(mpesaConfiguration.getResponseType());
        registerUrlRequest.setShortCode(mpesaConfiguration.getShortCode());
        registerUrlRequest.setValidationURL(mpesaConfiguration.getValidationURL());
        RequestBody body = RequestBody.create(Objects.requireNonNull(HelperUtility.toJson(registerUrlRequest)),
                JSON_MEDIA_TYPE);
        Request request = new Request.Builder()
                .url(mpesaConfiguration.getRegisterUrlEndpoint())
                .post(body)
                .addHeader("Authorization", String.format("%s %s", BEARER_AUTH_STRING, accessTokenResponse.getAccessToken()))
                .addHeader("Content-Type", "application/json")
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return null;
            }

            assert response.body() != null;
            String responseBodyString = Objects.requireNonNull(response.body()).string();
            System.out.println("Response body: " + responseBodyString);
            return objectMapper.readValue(responseBodyString, RegisterUrlResponse.class);
        } catch (IOException e) {
            return null;
        }

    }




    public CommonSyncResponse checkAccountBalance() {

        CheckAccountBalanceRequest checkAccountBalanceRequest = new CheckAccountBalanceRequest();
        checkAccountBalanceRequest.setInitiator(mpesaConfiguration.getB2cInitiatorName());
        checkAccountBalanceRequest.setSecurityCredential(mpesaConfiguration.getSecurityCredentials());
        checkAccountBalanceRequest.setCommandID(Constants.ACCOUNT_BALANCE_COMMAND);
        checkAccountBalanceRequest.setPartyA(mpesaConfiguration.getShortCode());
        checkAccountBalanceRequest.setIdentifierType(Constants.SHORT_CODE_IDENTIFIER);
        checkAccountBalanceRequest.setRemarks("Check Account Balance.");
        checkAccountBalanceRequest.setQueueTimeOutURL(mpesaConfiguration.getB2cQueueTimeoutUrl());
        checkAccountBalanceRequest.setResultURL(mpesaConfiguration.getB2cResultUrl());

        AccessTokenResponse accessTokenResponse = getAccessToken();

        RequestBody body = RequestBody.create(Objects.requireNonNull(HelperUtility.toJson(checkAccountBalanceRequest)),
                JSON_MEDIA_TYPE);

        Request request = new Request.Builder()
                .url(mpesaConfiguration.getCheckAccountBalanceUrl())
                .post(body)
                .addHeader(AUTHORIZATION_HEADER_STRING, String.format("%s %s", BEARER_AUTH_STRING, accessTokenResponse.getAccessToken()))
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return null;
            }

            assert response.body() != null;
            System.out.println(response);
            String responseBodyString = Objects.requireNonNull(response.body()).string();
            return objectMapper.readValue(responseBodyString, CommonSyncResponse.class);
        } catch (IOException e) {
            return null;
        }

    }


    public CommonSyncResponse performTransactionReversal(ReversalRequestDto reversalDetails) {

        reversalDetails.setInitiator(mpesaConfiguration.getB2cInitiatorName());
        reversalDetails.setSecurityCredential(mpesaConfiguration.getSecurityCredentials());
        reversalDetails.setCommandID(Constants.TRANSACTION_REVERSAL_COMMAND);
        reversalDetails.setReceiverParty(mpesaConfiguration.getShortCode());
        reversalDetails.setRecieverIdentifierType(Constants.SHORT_CODE_IDENTIFIER);
        reversalDetails.setQueueTimeOutURL(mpesaConfiguration.getB2cQueueTimeoutUrl());
        reversalDetails.setResultURL(mpesaConfiguration.getB2cResultUrl());

        AccessTokenResponse accessTokenResponse = getAccessToken();
        if (accessTokenResponse == null) {
            System.err.println("Failed to obtain access token for reversal request.");
            return null;
        }

        RequestBody body = RequestBody.create(
                Objects.requireNonNull(HelperUtility.toJson(reversalDetails)),
                JSON_MEDIA_TYPE
        );

        Request request = new Request.Builder()
                .url(mpesaConfiguration.getTransactionReversalUrl())
                .post(body)
                .addHeader(AUTHORIZATION_HEADER_STRING, String.format("%s %s", BEARER_AUTH_STRING, accessTokenResponse.getAccessToken()))
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.err.println("Reversal request failed with code: " + response.code() + ", message: " + response.message());
                if (response.body() != null) {
                    String errorBody = response.body().string();
                    System.err.println("Error body: " + errorBody);
                }
                return null;
            }

            String responseBodyString = Objects.requireNonNull(response.body()).string();
            return objectMapper.readValue(responseBodyString, CommonSyncResponse.class);
        } catch (IOException e) {
            System.err.println("IOException during reversal request: " + e.getMessage());
            return null;
        }

    }


}
