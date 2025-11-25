package com.fluxmartApi.auth;

import com.fluxmartApi.users.Role;
import jakarta.persistence.Column;
import lombok.Data;

@Data
public class CurrentUserResponseDto {
    private Integer id;
    private String username;
    private String email;
    private Role role;
}
