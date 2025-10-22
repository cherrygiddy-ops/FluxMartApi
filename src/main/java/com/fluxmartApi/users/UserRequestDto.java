package com.fluxmartApi.users;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRequestDto {
    @NotNull
   private String username;
    @NotNull
    private String email;
    @NotNull
    private String password;
}
