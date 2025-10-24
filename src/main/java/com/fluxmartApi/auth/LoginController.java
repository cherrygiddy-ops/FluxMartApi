package com.fluxmartApi.auth;

import com.fluxmartApi.auth.jwt.JwtConfig;
import com.fluxmartApi.auth.jwt.JwtResponseDto;
import com.fluxmartApi.auth.jwt.JwtService;
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
    private final AuthenticationManager authenticationManager;
    private final JwtService service;
    private final UserRepository userRepository;
    private  final UserMapper mapper;
    private final JwtConfig config;

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> login(@Valid  @RequestBody LoginRequestDto requestDto, HttpServletResponse response){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestDto.getEmail(),requestDto.getPassword()));

        var user =userRepository.findByEmail(requestDto.getEmail()).orElseThrow();
        var token =service.generateAccessTokens(user);
        var refresh = service.generateRefreshTokens(user);

        var cookie = new Cookie("refreshToken",refresh);
        cookie.setSecure(true);
        cookie.setMaxAge(config.getRefreshExpiration());
        cookie.setPath("/auth/refresh");

        response.addCookie(cookie);

        return ResponseEntity.ok().body(new JwtResponseDto(token));
    }

    @PostMapping("/validate")
    public boolean validateToken(@Valid  @RequestHeader("Authorization") String authHeader){
        System.out.println("validator is called");
        var token = authHeader.replace("Bearer ","");
        return service.validateToken(token);
    }

    @GetMapping("/currentUser")
    public UserResponseDto getCurrentUser(){
        var id =(Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var user = userRepository.findById(id).orElseThrow();
        return mapper.toDto(user);
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
