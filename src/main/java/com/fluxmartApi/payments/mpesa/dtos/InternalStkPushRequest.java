package com.fluxmartApi.payments.mpesa.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class InternalStkPushRequest{

//	@JsonProperty("Amount")
//	private BigDecimal amount;

	@JsonProperty("PhoneNumber")
	private String phoneNumber;
}