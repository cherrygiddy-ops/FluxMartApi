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
    private  final  LoginMapper loginMapper;

    public UserEntity getCurrentUser(){
        var userId = Integer.parseInt(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
         return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }
    public CurrentUserResponseDto getCurrentUserResponse() {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if ("anonymousUser".equals(principal)) {
            throw new UserNotFoundException();
        }

        var userId = Integer.parseInt(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        var user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        return loginMapper.toDto(user);
    }
    public Jwt getJwt(LoginRequestDto requestDto, HttpServletResponse response) throws AccountNotVerifiedException {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestDto.getEmail(), requestDto.getPassword()));

        var user =userRepository.findByEmail(requestDto.getEmail()).orElseThrow(UserNotFoundException::new);

        if (!user.isVerified()) {
            throw new AccountNotVerifiedException();
        }


        var token =service.generateAccessTokens(user);
        var refreshToken = service.generateRefreshTokens(user);

        String refreshTokenValue = refreshToken.toString();
        String cookieString = String.format(
                "refreshToken=%s; HttpOnly; Secure; SameSite=None; Path=/; Max-Age=%d",
                refreshTokenValue,
                config.getRefreshExpiration()
        );
        response.addHeader("Set-Cookie", cookieString);

        response.addHeader("Set-Cookie", cookieString);

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
