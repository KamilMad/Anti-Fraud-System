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

    public UserDto saveUser(User user) {
        User newUser = new User();
        copyUserProperties(user, newUser);
        setEncodedPassword(user, newUser);
        setRoleAndEnabled(newUser);

        return mapToDto(userRepository.save(newUser));
    }

    private void copyUserProperties(User source, User destination) {
        destination.setId(source.getId());
        destination.setName(source.getName());
        destination.setUsername(source.getUsername().toLowerCase());
    }

    private void setEncodedPassword(User source, User destination) {
        String encodedPassword = passwordEncoder.encode(source.getPassword());
        destination.setPassword(encodedPassword);
    }

    private void setRoleAndEnabled(User user) {
        if (!roleService.roleExist("ADMINISTRATOR")) {
            setAdministratorRoleAndEnabled(user);
        } else {
            setMerchantRoleAndDisabled(user);
        }
    }

    private void setAdministratorRoleAndEnabled(User user) {
        Role administratorRole = roleService.getOrCreateRole("ADMINISTRATOR");
        user.setRole(administratorRole);
        user.setEnabled(true);
    }

    private void setMerchantRoleAndDisabled(User user) {
        Role merchantRole = roleService.getOrCreateRole("MERCHANT");
        user.setRole(merchantRole);
        user.setEnabled(false);
    }
    public UserDto updateRole(UserRoleDto user){

        User eUser = userRepository.findByUsername(user.getUsername()).
                orElseThrow(() ->new UsernameNotFoundException("User not found with username " + user.getUsername()));

        validateUserRole(user.getRole(), eUser);

        String newRole = user.getRole();
        Role roleToUpdate = getRoleForUserRole(newRole);

        eUser.setRole(roleToUpdate);

        return mapToDto(userRepository.save(eUser));
    }

    private Role getRoleForUserRole(String userRole) {
        if (userRole.equals("SUPPORT")) {
            return roleService.getOrCreateRole("SUPPORT");
        } else {
            return roleService.getOrCreateRole("MERCHANT");
        }
    }

    private boolean isValidRole(String role) {
        return role.equals("SUPPORT") || role.equals("MERCHANT");
    }

    private void validateUserRole(String newRole, User eUser) {
        if (!isValidRole(newRole)) {
            throw new IllegalArgumentException("Illegal. eUser role is " + eUser.getRole().getName());
        }

        if (eUser.getRole().getName().equals(newRole)) {
            throw new RoleAlreadyAssignedException("Role already assigned to user");
        }
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
