package com.fluxmartApi.payments.mpesa.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InternalTransactionStatusRequest {

	@JsonProperty("BillRefNumber")
    private String BillRefNumber;
}