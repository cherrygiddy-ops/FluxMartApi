package com.fluxmartApi.payments;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class WebhookEventRequest {
    private String payload;
    private Map<String,String> headerSignature;
}
