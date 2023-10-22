package com.example.AntiFraudSystem.controller;

import com.example.AntiFraudSystem.controllers.AuthUserController;
import com.example.AntiFraudSystem.model.Role;
import com.example.AntiFraudSystem.model.User;
import com.example.AntiFraudSystem.payload.UserDto;
import com.example.AntiFraudSystem.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthUserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UserService userService;

    User userOne;
    User userTwo;

    @BeforeEach
    void setUp(){

        userOne = new User(1L,"Kamil", "user", "password", new Role("ADMINISTRATOR"), false);
        userTwo = new User(1L,"Kamil", "user1", "password1", new Role("MERCHANT"), false);
    }

    @Test
    public void testRegisterUserSuccess() throws Exception {
        // Create a sample User object for testing
        User user = new User();
        user.setUsername(userOne.getUsername());
        user.setPassword(userOne.getPassword());
        user.setName(userOne.getName());

        // Mock the behavior of userService
        when(userService.userExists("user")).thenReturn(false);
        when(userService.saveUser(userOne)).thenReturn(new UserDto(1L, "Kamil", "user", "ADMINISTRATOR"));

        // Perform the POST request
        mockMvc.perform(post("/api/auth/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"user\",\"password\":\"password\",\"name\":\"Kamil\"}")
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated());
    }
}
