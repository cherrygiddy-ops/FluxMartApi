package com.fluxmartApi.auth;

import com.fluxmartApi.auth.jwt.Jwt;
import com.fluxmartApi.auth.jwt.JwtConfig;
import com.fluxmartApi.auth.jwt.JwtResponseDto;
import com.fluxmartApi.auth.jwt.JwtService;
import com.fluxmartApi.users.UserEntity;
import com.fluxmartApi.users.UserMapper;
import com.fluxmartApi.users.UserRepository;
import com.fluxmartApi.users.UserResponseDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
@AllArgsConstructor
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final JwtConfig config;
    private final AuthenticationManager authenticationManager;
    private final JwtService service;

    public UserEntity getCurrentUser(){
        var userId= (Integer)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
         return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }
    public Jwt getJwt(LoginRequestDto requestDto, HttpServletResponse response) throws AccountNotVerifiedException {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestDto.getEmail(), requestDto.getPassword()));

        var user =userRepository.findByEmail(requestDto.getEmail()).orElseThrow(UserNotFoundException::new);

        if (!user.isVerified()) {
            throw new AccountNotVerifiedException();
        }


        var token =service.generateAccessTokens(user);
        var refreshToken = service.generateRefreshTokens(user);

        var cookie = new Cookie("refreshToken",refreshToken.toString());
        cookie.setSecure(true);
        cookie.setMaxAge(config.getRefreshExpiration());
        cookie.setPath("/auth/refresh");

        response.addCookie(cookie);
        return token;
    }

    public Jwt refreshToken(String refreshToken) {
        var jwt = service.parseToken(refreshToken);
        if (jwt==null || jwt.isExpired())
            throw new InvalidTokenException();
        var userId = jwt.getUserId();
        var user = userRepository.findById(userId).orElse(null);
        if (user == null)
            throw  new UserNotFoundException();
        return service.generateAccessTokens(user);
    }
}
