package com.fluxmartApi.users;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping()
    public ResponseEntity<UserResponseDto> registerUser(@RequestBody UserRegistrationRequestDto requestDto){
       var response = userService.registerUser(requestDto);
       return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @ExceptionHandler(UserExistsException.class)
    public ResponseEntity<?> handleUserNotFound() {
        return ResponseEntity.status(HttpStatus.FOUND).body("User Exists");
    }
}
