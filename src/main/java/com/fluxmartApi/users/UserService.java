package com.fluxmartApi.users;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    public List<UserResponseDto> getAllUsers() {
        var users = userRepository.findAll();

        return users.stream().map(userMapper::toDto).toList();
    }
}
