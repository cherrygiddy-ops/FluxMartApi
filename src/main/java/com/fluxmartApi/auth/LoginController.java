package com.fluxmartApi.auth;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class LoginController {
    private AuthenticationManager authenticationManager;
    private final JwtService service;

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> login(@Valid  @RequestBody LoginRequestDto requestDto){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestDto.getEmail(),requestDto.getPassword()));
        var token =service.generateTokens(requestDto.getEmail());

        return ResponseEntity.ok().body(new JwtResponseDto(token));
    }

    @PostMapping("/validate")
    public boolean validateToken(@Valid  @RequestHeader("Authorization") String authHeader){
        var token = authHeader.replace("Bearer ","");
        return service.validateToken(token);
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

}
