package com.fluxmartApi.auth.jwt;

import com.fluxmartApi.users.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;


public class Jwt {
    private final Claims claims;
    private final SecretKey secretKey;

    public Jwt(SecretKey secretKey, Claims claims) {
        this.secretKey = secretKey;
        this.claims = claims;
    }

    public boolean isExpired(){
        return claims.getExpiration().after(new Date());
    }

    public Integer getUserId(){
        return  Integer.parseInt(claims.getSubject());
    }

    public Role getRole(){
       return Role.valueOf(claims.get("role",String.class));
    }

    public String toString() {
        return Jwts.builder().claims(claims).signWith(secretKey).compact();
    }
}
