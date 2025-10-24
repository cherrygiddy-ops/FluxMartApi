package com.fluxmartApi.auth;


import com.fluxmartApi.users.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {
    @Value("${spring.jwt.secretKey}")
    private String secret;

    public String generateTokens(UserEntity entity){
        var expiration =60*60*24;
     return    Jwts.builder()
                .subject(String.valueOf(entity.getId()))
                .claim("name",entity.getUsername())
                .claim("email",entity.getEmail())
                .expiration(new Date(System.currentTimeMillis()+1000*expiration))
                .issuedAt(new Date())
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
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
                  .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                  .build()
                  .parseSignedClaims(token)
                  .getPayload();
    }

    public Integer getUserIdFromToken(String token) {
      return  Integer.parseInt(getClaims(token).getSubject());
    }
}
