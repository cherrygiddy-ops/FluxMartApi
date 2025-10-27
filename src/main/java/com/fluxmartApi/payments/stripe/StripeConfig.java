package com.fluxmartApi.payments.stripe;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration()
@ConfigurationProperties(prefix = "stripe")
public class StripeConfig {
    private String secretKey;
    private String webhooksecretkey;

    @PostConstruct
    private void init(){
        Stripe.apiKey =  secretKey;
    }
}
