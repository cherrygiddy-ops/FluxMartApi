package com.fluxmartApi.payments.mpesa.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SimulateTransactionRequest {

    @JsonProperty("ShortCode")
    private String shortCode;

    @JsonProperty("Msisdn")
    private String msisdn;

    @JsonProperty("BillRefNumber")
    private String billRefNumber;

    @JsonProperty("Amount")
    private BigDecimal amount;

    @JsonProperty("CommandID")
    private String commandID;
}