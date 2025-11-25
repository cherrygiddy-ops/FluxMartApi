package com.fluxmartApi.auth;

import com.fluxmartApi.users.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoginMapper {
    CurrentUserResponseDto toDto (UserEntity user);
}
