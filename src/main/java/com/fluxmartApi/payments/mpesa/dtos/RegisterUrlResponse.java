package com.fluxmartApi.payments.mpesa.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RegisterUrlResponse{

	@JsonProperty("ResponseCode")
	private String responseCode;

	@JsonProperty("ResponseDescription")
	private String responseDescription;

	@JsonProperty("OriginatorCoversationID")
	private String originatorCoversationID;
}