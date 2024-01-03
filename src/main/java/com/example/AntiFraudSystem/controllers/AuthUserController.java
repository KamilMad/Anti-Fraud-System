package com.example.AntiFraudSystem.controllers;

import com.example.AntiFraudSystem.model.User;
import com.example.AntiFraudSystem.payload.*;
import com.example.AntiFraudSystem.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthUserController {

    private final UserService userService;

    @Autowired
    public AuthUserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user){

        if (userService.userExists(user.getUsername())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User exists");
        }

        UserDTO userDto = userService.saveUser(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }

    @GetMapping("/list")
    public ResponseEntity<List<UserDTO>> getAllAvailableAuthUsers(){

        List<UserDTO> users =  userService.findAll();
        users.sort(Comparator.comparing(UserDTO::id));

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(users);
    }

    @DeleteMapping("/user/{username}")
    public ResponseEntity<UserDeleteDTO> deleteUser(@PathVariable String username){
        userService.deleteUserByUsername(username);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new UserDeleteDTO(username, "Deleted successfully!"));
    }

    @PutMapping("/role")
    public ResponseEntity<UserDTO> changeRole(@RequestBody UserRoleDto user){
        return new ResponseEntity<>(userService.updateRole(user), HttpStatus.OK);
    }

    @PutMapping("/access")
    public ResponseEntity<StatusDTO> updateUserAccess(@RequestBody UserAccessRequestDTO userAccessRequestDTO){
        return new ResponseEntity<>(userService.changeAccess(userAccessRequestDTO), HttpStatus.OK);
    }
}