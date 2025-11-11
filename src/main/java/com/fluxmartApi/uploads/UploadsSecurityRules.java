package com.fluxmartApi.uploads;

import com.fluxmartApi.common.SecurityRules;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

@Component
public class UploadsSecurityRules implements SecurityRules {
    @Override
    public void config(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        registry.requestMatchers("/uploads/**").permitAll();
        //registry.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
    }
}
