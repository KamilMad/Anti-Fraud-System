package com.example.AntiFraudSystem.controllers;

import com.example.AntiFraudSystem.model.User;
import com.example.AntiFraudSystem.payload.UserDto;
import com.example.AntiFraudSystem.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthUserController {

    private final UserService userService;

    @Autowired
    public AuthUserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user")
    public ResponseEntity<UserDto> registerUser(@Valid @RequestBody User user){
        UserDto userDto = userService.saveUser(user);

        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }
}
