package com.fluxmartApi.admin;

import com.fluxmartApi.users.UserResponseDto;
import com.fluxmartApi.users.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminController {
    private UserService userService;


    @GetMapping ("/users")
    public List<UserResponseDto> getAllUsers (){
        return userService.getAllUsers();
    }
}
