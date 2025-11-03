package com.fluxmartApi.users;

import com.fluxmartApi.auth.InvalidTokenException;
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


    @GetMapping("/verify")
    public ResponseEntity<String >verify(@RequestParam String token) {
     return ResponseEntity.ok(userService.verify(token));
    }

    @ExceptionHandler(UserExistsException.class)
    public ResponseEntity<?> handleUserNotFound() {
        return ResponseEntity.status(HttpStatus.FOUND).body("User Exists");
    }
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<?> handleInvalidToken (){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Expired  Token");
    }

}
