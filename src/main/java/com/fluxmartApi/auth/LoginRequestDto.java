package com.fluxmartApi.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDto {
    @NotBlank(message = "email required")
    @Email
    private String email;

    @NotBlank(message = "password required")
    private String password;
}
