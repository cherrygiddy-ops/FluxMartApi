package com.fluxmartApi.payments.mpesa.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExternalStkPushRequest {

    @JsonProperty("TransactionType")
    private String transactionType;

    @JsonProperty("Amount")
    private BigDecimal amount;

    @JsonProperty("CallBackURL")
    private String callBackURL;

    @JsonProperty("PhoneNumber")
    private String phoneNumber;

    @JsonProperty("PartyA")
    private String partyA;

    @JsonProperty("PartyB")
    private String partyB;

    @JsonProperty("AccountReference")
    private String accountReference;

    @JsonProperty("TransactionDesc")
    private String transactionDesc;

    @JsonProperty("BusinessShortCode")
    private String businessShortCode;

    @JsonProperty("Timestamp")
    private String timestamp;

    @JsonProperty("Password")
    private String password;
}