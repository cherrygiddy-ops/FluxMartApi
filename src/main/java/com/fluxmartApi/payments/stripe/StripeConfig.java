package com.fluxmartApi.payments.stripe;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration()
@ConfigurationProperties(prefix = "stripe")
public class StripeConfig {
    private static final Logger log = LoggerFactory.getLogger(StripePaymentService.class);
    private String secretKey;
    private String webhooksecretkey;

    @PostConstruct
    private void init() {
        Stripe.apiKey = secretKey;
        log.info("Stripe webhook secret loaded: {}", webhooksecretkey);
    }
}
