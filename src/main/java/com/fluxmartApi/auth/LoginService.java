package com.fluxmartApi.auth;

import com.fluxmartApi.users.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LoginService {
    private final UserRepository repository;
    private final PasswordEncoder encoder;

    public boolean login(LoginRequestDto requestDto) {
        var user = repository.findByEmail(requestDto.getEmail()).orElseThrow(UserNotFoundException::new);
        if (encoder.matches(requestDto.getPassword(), user.getPassword()))
            throw new CredentialsMatchExceptions();
        throw new CredentialsDontMatchExceptions();
    }
}
