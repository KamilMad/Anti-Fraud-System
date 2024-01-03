package com.example.AntiFraudSystem.service;

import com.example.AntiFraudSystem.errors.AdministratorBlockedException;
import com.example.AntiFraudSystem.errors.RoleAlreadyAssignedException;
import com.example.AntiFraudSystem.model.Role;
import com.example.AntiFraudSystem.model.User;
import com.example.AntiFraudSystem.payload.StatusDTO;
import com.example.AntiFraudSystem.payload.UserAccessRequestDTO;
import com.example.AntiFraudSystem.payload.UserDTO;
import com.example.AntiFraudSystem.payload.UserRoleDto;
import com.example.AntiFraudSystem.repositories.UserRepository;
import com.example.AntiFraudSystem.services.RoleService;
import com.example.AntiFraudSystem.services.UserService;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        UserDTO expectedUserDTO = new UserDTO(1L, "name", "username", "ADMINISTRATOR");

        when(passwordEncoder.encode(any())).thenReturn(password);
        when(roleService.roleExist(any(String.class))).thenReturn(false);
        when(roleService.getOrCreateRole("ADMINISTRATOR")).thenReturn(new Role("ADMINISTRATOR"));
        when(userRepository.save(user)).thenReturn(user);

        UserDTO actualUserDTO = userService.saveUser(user);

        assertNotNull(actualUserDTO);
        assertEquals(expectedUserDTO, actualUserDTO);
    }

    @Test
    public void testSaveUser_AdministratorExists() {
        String password = "$2a$12$TuqUl2jroR5Fx0ws1B8aLuIRPjqHmIw8MbPDgAKMf1fjGKdE9HQEi";

        User user = createUser();
        user.setRole(new Role("MERCHANT"));
        user.setEnabled(false);

        UserDTO expectedUserDTO = new UserDTO(1L, "name", "username", "MERCHANT");
        when(passwordEncoder.encode(any())).thenReturn(password);
        when(roleService.roleExist(any(String.class))).thenReturn(true);
        when(roleService.getOrCreateRole("MERCHANT")).thenReturn(new Role("MERCHANT"));
        when(userRepository.save(user)).thenReturn(user);

        UserDTO actualUserDTO = userService.saveUser(user);

        assertNotNull(actualUserDTO);
        assertEquals(expectedUserDTO, actualUserDTO);
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

        UserDTO expectedUserDTO = new UserDTO(1l, user.getName(), user.getUsername(), supportRole.getName());

        when(userRepository.findByUsername(userRoleDto.getUsername())).thenReturn(Optional.of(user));
        when(roleService.getOrCreateRole("SUPPORT")).thenReturn(supportRole);
        when(userRepository.save(user)).thenReturn(user);

        UserDTO actualUserDTO = userService.updateRole(userRoleDto);
        assertEquals(expectedUserDTO.getRole(), actualUserDTO.getRole());
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

        UserDTO expectedUserDTO = new UserDTO(1l, user.getName(), user.getUsername(), merchantRole.getName());

        when(userRepository.findByUsername(userRoleDto.getUsername())).thenReturn(Optional.of(user));
        when(roleService.getOrCreateRole("MERCHANT")).thenReturn(merchantRole);
        when(userRepository.save(user)).thenReturn(user);

        UserDTO actualUserDTO = userService.updateRole(userRoleDto);
        assertEquals(expectedUserDTO.getRole(), actualUserDTO.getRole());
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

        List<UserDTO> actualList = userService.findAll();

        assertNotNull(actualList);
        assertEquals(2, actualList.size());
    }

    @Test
    public void testFindAll_ReturnEmptyList() {
        List<User> userList = new ArrayList<>();
        when(userRepository.findAll().stream().collect(Collectors.toList())).thenReturn(userList);

        List<UserDTO> actualList = userService.findAll();

        assertTrue(actualList.isEmpty());
    }

    @Test
    public void testDeleteUserByUsername_UserDoesNotExist() {
        String username = "username";

        when(userRepository.findByUsername(username)).thenThrow(new UsernameNotFoundException("User not found with username: " + username));
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,() -> userService.deleteUserByUsername(username));

        assertEquals("User not found with username: " + username, exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
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

    @Test
    public void testChangeAccess_UserDoesNotExist() {
        UserAccessRequestDTO user = new UserAccessRequestDTO("username", "");
        when(userRepository
                .findByUsername(user.username()))
                .thenThrow(new UsernameNotFoundException("User not found with username: " + user.username()));

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,() -> userService.changeAccess(user));
        assertEquals("User not found with username: " + user.username(), exception.getMessage());
        verifyNoMoreInteractions(userRepository);
        verify(userRepository, times(1)).findByUsername(user.username());
    }

    @Test
    public void testChangeAccess_UserIsAdministrator() {
        User administrator = createUser();
        administrator.setRole(new Role("ADMINISTRATOR"));

        UserAccessRequestDTO user = new UserAccessRequestDTO("username", "");

        when(userRepository.findByUsername(administrator.getUsername())).thenReturn(Optional.of(administrator));

        AdministratorBlockedException exception = assertThrows(AdministratorBlockedException.class,() -> userService.changeAccess(user));
        assertEquals("Administrator cannot be blocked", exception.getMessage());
        verifyNoMoreInteractions(userRepository);
        verify(userRepository, times(1)).findByUsername(user.username());
    }

    @ParameterizedTest
    @MethodSource("provideUserRolesAndOperations")
    public void testChangeAccess_SuccessfullyChangeUserAccess(String userRole, String userOperation) {
        User user = createUser();
        user.setRole(new Role(userRole));
        user.setEnabled(false);

        UserAccessRequestDTO userAccessRequestDTO = new UserAccessRequestDTO("username", userOperation);
        StatusDTO expectedStatus = new StatusDTO("User " + user.getUsername() + " " + userAccessRequestDTO.operation().toLowerCase() + "ed!");

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        StatusDTO actualStatus = userService.changeAccess(userAccessRequestDTO);

        assertEquals(expectedStatus, actualStatus);

    }

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setName("name");
        return user;
    }

    private static Stream<Arguments> provideUserRolesAndOperations() {
        return Stream.of(
                Arguments.of("MERCHANT", "LOCK"),
                Arguments.of("MERCHANT", "UNLOCK"),
                Arguments.of("SUPPORT", "LOCK"),
                Arguments.of("SUPPORT", "UNLOCK")
                // Add more roles and operations as needed
        );
    }
}
