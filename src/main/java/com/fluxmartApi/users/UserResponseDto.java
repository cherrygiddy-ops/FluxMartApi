package com.fluxmartApi.users;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class UserResponseDto {

    private Integer id;
    private String username;
    private String email;
}
