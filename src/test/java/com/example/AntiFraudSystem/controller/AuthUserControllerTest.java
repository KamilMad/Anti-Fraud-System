package com.example.AntiFraudSystem.controller;

import com.example.AntiFraudSystem.controllers.AuthUserController;
import com.example.AntiFraudSystem.model.Role;
import com.example.AntiFraudSystem.model.User;
import com.example.AntiFraudSystem.payload.*;
import com.example.AntiFraudSystem.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthUserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UserService userService;

    private User userOne;

    @BeforeEach
    void setUp(){
        userOne = new User(1L,"Kamil", "user", "password", new Role("ADMINISTRATOR"), false);
    }

    @Test
    public void testRegisterUser_Successfully() throws Exception {

        UserDto userDto = new UserDto(1L, "Kamil", "user", "ADMINISTRATOR");

        // Mock the behavior of userService
        when(userService.userExists("user")).thenReturn(false);
        when(userService.saveUser(userOne)).thenReturn(userDto);

        // Perform the POST request
        mockMvc.perform(post("/api/auth/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userOne))
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated());
    }

    @Test
    public void testGetAllAvailableAuthUsers_Successfully() throws Exception {
        UserDto userDto1 = createUserDto(1L, "user1", "username1", "SUPPORT");
        UserDto userDto2 = createUserDto(2L, "user2", "username2", "MERCHANT");
        UserDto userDto3 = createUserDto(3L, "user3", "username3", "ADMINISTRATOR");

        List<UserDto> users = new ArrayList<>();
        users.add(userDto1);
        users.add(userDto2);
        users.add(userDto3);

        when(userService.findAll()).thenReturn(users);

        mockMvc.perform(get("/api/auth/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("user1"))
                .andExpect(jsonPath("$[1].name").value("user2"))
                .andExpect(jsonPath("$[2].name").value("user3"));
    }

    @Test
    public void testDeleteUser_SuccessfulDeletion() throws Exception {

        String usernameToDelete = "userToDelete";
        UserDeleteDto expectedResponse = new UserDeleteDto(usernameToDelete, "Deleted successfully!");

        mockMvc.perform(delete("/api/auth/user/{username}", usernameToDelete))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(expectedResponse)));
    }

    @Test
    public void testRegisterUser_UserExists() throws Exception {
        when(userService.userExists("user")).thenReturn(true);

        mockMvc.perform(post("/api/auth/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userOne))) // Convert user object to JSON
                        .andExpect(status().isConflict())
                        .andExpect(content().string("User exists"));
    }

    @Test
    public void testChangeRole_SuccessfulUpdate() throws Exception {
        UserRoleDto userRoleDto =new UserRoleDto();
        userRoleDto.setRole("SUPPORT");
        userRoleDto.setUsername("user");

        UserDto updatedUserDto= new UserDto(1L, "Kamil", "user", "SUPPORT");

        when(userService.updateRole(userRoleDto)).thenReturn(updatedUserDto);
        
        mockMvc.perform(put("/api/auth//role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userRoleDto))) // Convert userRoleDto to JSON
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(updatedUserDto)));
    }

    @ParameterizedTest
    @CsvSource({
            "username, LOCK",
            "username, UNLOCK"
    })
    public void testUpdateUserAccess_SuccessfulUpdate(String username, String operation) throws Exception {
        UserAccessRequest userAccessRequest = new UserAccessRequest();
        userAccessRequest.setUsername(username);
        userAccessRequest.setOperation(operation);

        StatusDto statusDto = new StatusDto("User " + userAccessRequest.getUsername() + " " + userAccessRequest.getOperation().toLowerCase() + "ed!");

        when(userService.changeAccess(userAccessRequest)).thenReturn(statusDto);

        mockMvc.perform(put("/api/auth/access")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userAccessRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(statusDto)));

    }
    

    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private UserDto createUserDto(Long id, String name, String username, String role) {
        UserDto userDto = new UserDto();
        userDto.setId(id);
        userDto.setName(name);
        userDto.setUsername(username);
        userDto.setRole(role);
        return userDto;
    }
}
