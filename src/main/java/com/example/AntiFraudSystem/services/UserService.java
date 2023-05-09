package com.example.AntiFraudSystem.services;

import com.example.AntiFraudSystem.model.User;
import com.example.AntiFraudSystem.payload.UserDto;
import com.example.AntiFraudSystem.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto saveUser(User user){
        User savedUser = userRepository.save(user);

        return mapToDto(savedUser);
    }

    private UserDto mapToDto(User user){
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setUsername(user.getUsername());

        return userDto;
    }
}
