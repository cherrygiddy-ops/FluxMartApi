package com.fluxmartApi.auth.jwt;

import io.jsonwebtoken.security.Keys;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

@Configuration
@Data
@ConfigurationProperties(prefix = "spring.jwt")
public class JwtConfig {
    private String secretKey;
    private int refreshExpiration;
    private int accessExpiration;

    public SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}
