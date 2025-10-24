package com.fluxmartApi.auth.jwt;


import com.fluxmartApi.users.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
@AllArgsConstructor
@Service
public class JwtService {
 private final JwtConfig config;

    public String generateAccessTokens(UserEntity entity){
     return generateToken(entity, config.getAccessExpiration());
    }

    public String generateRefreshTokens(UserEntity entity){
        return generateToken(entity, config.getRefreshExpiration());
    }

    private String generateToken(UserEntity entity, int expiration) {
        return Jwts.builder()
                .subject(String.valueOf(entity.getId()))
                .claim("name", entity.getUsername())
                .claim("email", entity.getEmail())
                .expiration(new Date(System.currentTimeMillis() + 1000L * expiration))
                .issuedAt(new Date())
                .signWith(config.getSecretKey())
                .compact();
    }

    public Boolean validateToken(String token){
        try{
            var claims = getClaims(token);
            return claims.getExpiration().after(new Date());
        }catch (JwtException ex){
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                  .verifyWith(config.getSecretKey())
                  .build()
                  .parseSignedClaims(token)
                  .getPayload();
    }

    public Integer getUserIdFromToken(String token) {
      return  Integer.parseInt(getClaims(token).getSubject());
    }
}
