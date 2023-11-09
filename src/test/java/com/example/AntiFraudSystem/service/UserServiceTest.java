package com.example.AntiFraudSystem.service;

import com.example.AntiFraudSystem.errors.RoleAlreadyAssignedException;
import com.example.AntiFraudSystem.model.Role;
import com.example.AntiFraudSystem.model.User;
import com.example.AntiFraudSystem.payload.UserDto;
import com.example.AntiFraudSystem.payload.UserRoleDto;
import com.example.AntiFraudSystem.repositories.UserRepository;
import com.example.AntiFraudSystem.services.RoleService;
import com.example.AntiFraudSystem.services.UserService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleService roleService;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private static Validator validator;

    @BeforeAll
    public static void setupValidatorInstance() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void testSaveUse_AdministratorDoesNotExists() {
        String password = "$2a$12$TuqUl2jroR5Fx0ws1B8aLuIRPjqHmIw8MbPDgAKMf1fjGKdE9HQEi";

        User user = createUser();
        user.setRole(new Role("ADMINISTRATOR"));
        user.setEnabled(true);

        UserDto expectedUserDto = new UserDto(1L, "name", "username", "ADMINISTRATOR");

        when(passwordEncoder.encode(any())).thenReturn(password);
        when(roleService.roleExist(any(String.class))).thenReturn(false);
        when(roleService.getOrCreateRole("ADMINISTRATOR")).thenReturn(new Role("ADMINISTRATOR"));
        when(userRepository.save(user)).thenReturn(user);

        UserDto actualUserDto = userService.saveUser(user);

        assertNotNull(actualUserDto);
        assertEquals(expectedUserDto, actualUserDto);
    }

    @Test
    public void testSaveUser_AdministratorExists() {
        String password = "$2a$12$TuqUl2jroR5Fx0ws1B8aLuIRPjqHmIw8MbPDgAKMf1fjGKdE9HQEi";

        User user = createUser();
        user.setRole(new Role("MERCHANT"));
        user.setEnabled(false);

        UserDto expectedUserDto = new UserDto(1L, "name", "username", "MERCHANT");
        when(passwordEncoder.encode(any())).thenReturn(password);
        when(roleService.roleExist(any(String.class))).thenReturn(true);
        when(roleService.getOrCreateRole("MERCHANT")).thenReturn(new Role("MERCHANT"));
        when(userRepository.save(user)).thenReturn(user);

        UserDto actualUserDto = userService.saveUser(user);

        assertNotNull(actualUserDto);
        assertEquals(expectedUserDto, actualUserDto);
    }

    @Test
    public void testUpdateRole_UserNotFound() {

        UserRoleDto userRoleDto = new UserRoleDto();
        userRoleDto.setUsername("user");
        userRoleDto.setRole("MERCHANT");

        when(userRepository.findByUsername("user")).thenThrow(new UsernameNotFoundException("User not found with username " + userRoleDto.getUsername()));

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> userService.updateRole(userRoleDto));
        assertEquals("User not found with username " + userRoleDto.getUsername(), exception.getMessage());
        verifyNoInteractions(roleService);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void testUpdateRole_IllegalRole() {
        UserRoleDto userRoleDto = new UserRoleDto();
        userRoleDto.setUsername("user");
        userRoleDto.setRole("ROLE");

        User user = createUser();
        user.setEnabled(true);
        user.setRole(new Role(userRoleDto.getRole()));;

        when(userRepository.findByUsername(userRoleDto.getUsername())).thenReturn(Optional.of(user));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> userService.updateRole(userRoleDto));
        assertEquals("Illegal. eUser role is " +  user.getRole().getName(), exception.getMessage());
        verifyNoInteractions(roleService);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void testUpdateRole_RoleAlreadyAssigned() {
        UserRoleDto userRoleDto = new UserRoleDto();
        userRoleDto.setUsername("user");
        userRoleDto.setRole("MERCHANT");

        User user = createUser();
        user.setEnabled(true);
        user.setRole(new Role(userRoleDto.getRole()));

        when(userRepository.findByUsername(userRoleDto.getUsername())).thenReturn(Optional.of(user));

        RoleAlreadyAssignedException exception = assertThrows(RoleAlreadyAssignedException.class, () -> userService.updateRole(userRoleDto));
        assertEquals("Role already assigned to user", exception.getMessage());
        verifyNoInteractions(roleService);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void testUpdateRole_RoleIsSupport() {
        Role supportRole = new Role("SUPPORT");

        UserRoleDto userRoleDto = new UserRoleDto();
        userRoleDto.setUsername("user");
        userRoleDto.setRole(supportRole.getName());

        User user = createUser();
        user.setEnabled(true);
        user.setRole(new Role("MERCHANT"));

        UserDto expectedUserDto = new UserDto(1l, user.getName(), user.getUsername(), supportRole.getName());

        when(userRepository.findByUsername(userRoleDto.getUsername())).thenReturn(Optional.of(user));
        when(roleService.getOrCreateRole("SUPPORT")).thenReturn(supportRole);
        when(userRepository.save(user)).thenReturn(user);

        UserDto actualUserDto = userService.updateRole(userRoleDto);
        assertEquals(expectedUserDto.getRole(), actualUserDto.getRole());
    }

    @Test
    public void testUpdateRole_RoleIsMerchant() {
        Role merchantRole = new Role("MERCHANT");

        UserRoleDto userRoleDto = new UserRoleDto();
        userRoleDto.setUsername("user");
        userRoleDto.setRole(merchantRole.getName());

        User user = createUser();
        user.setEnabled(true);
        user.setRole(new Role("SUPPORT"));

        UserDto expectedUserDto = new UserDto(1l, user.getName(), user.getUsername(), merchantRole.getName());

        when(userRepository.findByUsername(userRoleDto.getUsername())).thenReturn(Optional.of(user));
        when(roleService.getOrCreateRole("MERCHANT")).thenReturn(merchantRole);
        when(userRepository.save(user)).thenReturn(user);

        UserDto actualUserDto = userService.updateRole(userRoleDto);
        assertEquals(expectedUserDto.getRole(), actualUserDto.getRole());
    }

    @Test
    public void testUserExists_UserExists() {
        User user = createUser();

        when(userRepository.findByUsername(user.getName())).thenReturn(Optional.of(user));

        boolean result = userService.userExists(user.getName());

        assertTrue(result);
    }

    @Test
    public void testUserExists_UserDoesNotExist() {
        User user = createUser();
        when(userRepository.findByUsername(user.getName())).thenReturn(Optional.empty());

        boolean result = userService.userExists(user.getName());

        assertFalse(result);
    }

    @Test
    public void testFindAll_ReturnExpectedList() {
        User user1 = createUser();
        user1.setEnabled(true);
        user1.setRole(new Role("Role1"));

        User user2 = createUser();
        user2.setEnabled(true);
        user2.setRole(new Role("Role2"));

        List<User> userList = new ArrayList<>();
        userList.add(user1);
        userList.add(user2);

        when(userRepository.findAll().stream().collect(Collectors.toList())).thenReturn(userList);

        List<UserDto> actualList = userService.findAll();

        assertNotNull(actualList);
        assertEquals(2, actualList.size());
    }

    @Test
    public void testFindAll_ReturnEmptyList() {
        List<User> userList = new ArrayList<>();
        when(userRepository.findAll().stream().collect(Collectors.toList())).thenReturn(userList);

        List<UserDto> actualList = userService.findAll();

        assertTrue(actualList.isEmpty());
    }

    @Test
    public void testDeleteUserByUsername_UserDoesNotExist() {
        User user = createUser();

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,() -> userService.deleteUserByUsername(user.getUsername()));

        assertEquals("User not found with username: " + user.getUsername(), exception.getMessage());
        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    public void testDeleteUserByUsername_UserExists() {
        User user = createUser();

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        userService.deleteUserByUsername(user.getUsername());

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(userRepository, times(1)).delete(user);
    }

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setName("name");
        return user;
    }
}
