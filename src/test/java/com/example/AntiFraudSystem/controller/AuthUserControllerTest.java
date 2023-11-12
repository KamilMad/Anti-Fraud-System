package com.example.AntiFraudSystem.controller;

import com.example.AntiFraudSystem.controllers.AuthUserController;
import com.example.AntiFraudSystem.model.Role;
import com.example.AntiFraudSystem.model.User;
import com.example.AntiFraudSystem.payload.UserDto;
import com.example.AntiFraudSystem.payload.UserRoleDto;
import com.example.AntiFraudSystem.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.when;
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
    private User userTwo;

    @BeforeEach
    void setUp(){

        userOne = new User(1L,"Kamil", "user", "password", new Role("ADMINISTRATOR"), false);
        userTwo = new User(1L,"Kamil", "user1", "password1", new Role("MERCHANT"), false);
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
    public void testRegisterUser_UserExists() throws Exception {
        when(userService.userExists("user")).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userOne))) // Convert user object to JSON
                        .andExpect(status().isConflict())
                        .andExpect(content().string("User exists"));
    }

    @Test
    public void testChangeRole_SuccessfulUpdate() throws Exception {
        // Arrange
        UserRoleDto userRoleDto =new UserRoleDto();
        userRoleDto.setRole("SUPPORT");
        userRoleDto.setUsername("user");

        UserDto updatedUserDto= new UserDto(1L, "Kamil", "user", "SUPPORT");

        when(userService.updateRole(userRoleDto)).thenReturn(updatedUserDto);
        
        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/auth//role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userRoleDto))) // Convert userRoleDto to JSON
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(updatedUserDto)));
    }

    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
