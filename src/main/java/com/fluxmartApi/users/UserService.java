package com.fluxmartApi.users;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;


    public UserResponseDto registerUser(UserRequestDto requestDto) {
        var user=userRepository.findByEmail(requestDto.getEmail()).orElse(null);
        if (user != null)
            throw new UserExistsException();
        var userR = userMapper.toEntity(requestDto);
        userRepository.save(userR);
        return userMapper.toDto(userR);
    }
}
