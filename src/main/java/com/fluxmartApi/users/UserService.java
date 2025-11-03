package com.fluxmartApi.users;

import com.fluxmartApi.auth.InvalidTokenException;
import com.fluxmartApi.auth.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService  {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;


    public UserResponseDto registerUser(UserRegistrationRequestDto requestDto)  {
        if (userRepository.existsByEmail(requestDto.getEmail()))
            throw new UserExistsException();
        var user = userMapper.toEntity(requestDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        user.setTokenExpiry(LocalDateTime.now().plusHours(24));
        userRepository.save(user);
        //emailService.sendVerificationEmail(email, token);
        return userMapper.toDto(user);
    }

    public List<UserResponseDto> getAllUsers() {
        var users = userRepository.findAll();

        return users.stream().map(userMapper::toDto).toList();
    }


    public String verify(String token) {
        var user = userRepository.findByVerificationToken(token).orElseThrow(UserNotFoundException::new);
        if (user.getTokenExpiry().isBefore(LocalDateTime.now())) {
           throw new InvalidTokenException();
        }

        user.setVerified(true);
        user.setVerificationToken(null);
        user.setTokenExpiry(null);
        userRepository.save(user);

        return "Account verified successfully";
    }
}
