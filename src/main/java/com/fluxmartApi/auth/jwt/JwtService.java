package com.fluxmartApi.auth.jwt;


import com.fluxmartApi.users.Role;
import com.fluxmartApi.users.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
@RequiredArgsConstructor
@Service
public class JwtService {
 private final JwtConfig config;

    public Jwt generateAccessTokens(UserEntity entity){
     return generateToken(entity, config.getAccessExpiration());
    }

    public Jwt generateRefreshTokens(UserEntity entity){
        return generateToken(entity, config.getRefreshExpiration());
    }

    private Jwt generateToken(UserEntity entity, int expiration) {
        var claims = Jwts.claims().subject(entity.getId().toString())
                .add("name",entity.getUsername())
                .add("email",entity.getEmail())
                .add("role",entity.getRole())
                .expiration(new Date(System.currentTimeMillis() + 1000L * expiration))
                .issuedAt(new Date())
                .build();
        return new Jwt(config.getSecretKey(), claims);
    }

    public Jwt  parseToken(String token){
        try {
            var claims = getClaims(token);
           return new Jwt(config.getSecretKey(), claims);
        }catch (JwtException ex){
            return null;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                  .verifyWith(config.getSecretKey())
                  .build()
                  .parseSignedClaims(token)
                  .getPayload();
    }


}
