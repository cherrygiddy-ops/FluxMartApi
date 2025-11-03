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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class LoginController {
    private final AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> login(@Valid  @RequestBody LoginRequestDto requestDto, HttpServletResponse response){
        try {
           var  token = authService.getJwt(requestDto, response);
        return ResponseEntity.ok().body(new JwtResponseDto(token.toString()));
        } catch (AccountNotVerifiedException e) {
            throw new RuntimeException(e);
        }
    }


    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue (value = "refreshToken") String refreshToken){
        var token=authService.refreshToken(refreshToken);
       return  ResponseEntity.ok(new JwtResponseDto(token.toString()));
    }

    @GetMapping("/currentUser")
    public UserEntity getCurrentUser(){
        return authService.getCurrentUser();
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFound (){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleCredentialsDontMatch (){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credentials Not Matching");
    }

    @ExceptionHandler(CredentialsMatchExceptions.class)
    public ResponseEntity<?> handleCredentialMatch (){
        return ResponseEntity.status(HttpStatus.OK).body("Authorized");
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<?> handleInvalidTokenMatch (){
        return ResponseEntity.status(HttpStatus.OK).body("invalid Tokens");
    }

    @ExceptionHandler(AccountNotVerifiedException.class)
    public ResponseEntity<?> handleAccountNotVerified (){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please verify your email before logging in.");
    }

}
