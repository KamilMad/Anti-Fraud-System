package com.example.AntiFraudSystem.services;

import com.example.AntiFraudSystem.errors.AdministratorBlockedException;
import com.example.AntiFraudSystem.errors.RoleAlreadyAssignedException;
import com.example.AntiFraudSystem.model.Role;
import com.example.AntiFraudSystem.model.User;
import com.example.AntiFraudSystem.payload.StatusDto;
import com.example.AntiFraudSystem.payload.UserAccessRequest;
import com.example.AntiFraudSystem.payload.UserDto;
import com.example.AntiFraudSystem.payload.UserRoleDto;
import com.example.AntiFraudSystem.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,  RoleService roleService, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDto saveUser(User user){

        User newUser = new User();

        newUser.setId(user.getId());
        newUser.setName(user.getName());
        newUser.setUsername(user.getUsername().toLowerCase());

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        newUser.setPassword(encodedPassword);

        if (!roleService.roleExist("ADMINISTRATOR")){
            Role administratorRole = roleService.getOrCreateRole("ADMINISTRATOR");
            newUser.setRole(administratorRole);
            newUser.setEnabled(true);
        }
        else {
            Role merchantRole = roleService.getOrCreateRole("MERCHANT");
            newUser.setRole(merchantRole);
            newUser.setEnabled(false);
        }

        return mapToDto(userRepository.save(newUser));
    }

    public UserDto updateRole(UserRoleDto user){

        User eUser = userRepository.findByUsername(user.getUsername()).
                orElseThrow(() ->new UsernameNotFoundException("User not found with username " + user.getUsername()));

        if (!user.getRole().equals("SUPPORT") && !user.getRole().equals("MERCHANT")){
            throw new IllegalArgumentException("Illegal. eUser role is " +  eUser.getRole().getName());
        }

        if (eUser.getRole().getName().equals(user.getRole())){
            throw new RoleAlreadyAssignedException("Role already assigned to user");
        }

        String newRole = user.getRole();

        if (newRole.equals("SUPPORT")){
            Role supportRole = roleService.getOrCreateRole("SUPPORT");
            eUser.setRole(supportRole);
        }
        else {
            Role merchantRole = roleService.getOrCreateRole("MERCHANT");
            eUser.setRole(merchantRole);
        }

        userRepository.save(eUser);

        return mapToDto(userRepository.save(eUser));
    }

    public boolean userExists(String username){
        Optional<User> user = userRepository.findByUsername(username);

        return user.isPresent();
    }

    public List<UserDto> findAll(){
        return userRepository
                .findAll()
                .stream()
                .map(this::mapToDto).collect(Collectors.toList());
    }

    public void deleteUserByUsername(String username){
        User user = userRepository.findByUsername(username).orElseThrow(()
                -> new UsernameNotFoundException("User not found with username: " + username));

        userRepository.delete(user);
    }

    public StatusDto changeAccess(UserAccessRequest userAccessRequest) {

        User user = userRepository.findByUsername(userAccessRequest.getUsername()).orElseThrow(()
                -> new UsernameNotFoundException("User not found with username: " + userAccessRequest.getUsername()));

        if (user.getRole().getName().equals("ADMINISTRATOR")){
            throw new AdministratorBlockedException("Administrator cannot be blocked");
        }

        user.setEnabled(!userAccessRequest.getOperation().equals("LOCK"));
        userRepository.save(user);

        return new StatusDto("User " + user.getUsername() + " " + userAccessRequest.getOperation().toLowerCase() + "ed!");
    }

    private UserDto mapToDto(User user){
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setUsername(user.getUsername());
        userDto.setRole(user.getRole().getName());

        return userDto;
    }
}
