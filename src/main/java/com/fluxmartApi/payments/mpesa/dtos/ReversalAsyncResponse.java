package com.fluxmartApi.payments.mpesa.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReversalAsyncResponse{

    @JsonProperty("Result")
    private Result result;
}