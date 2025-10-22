package com.fluxmartApi.users;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserEntity toEntity (UserRequestDto requestDto);

    UserResponseDto toDto(UserEntity userEntity);
}
