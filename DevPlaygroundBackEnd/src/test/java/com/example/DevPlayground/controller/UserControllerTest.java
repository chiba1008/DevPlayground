package com.example.DevPlayground.controller;

import com.example.DevPlayground.entity.User;
import com.example.DevPlayground.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    private List<User> testUsers;

    @BeforeEach
    void setUp() {
        UserController userController = new UserController(userService);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        
        User user1 = new User(123L, "test1", "test1@example.com");
        User user2 = new User(124L, "test2", "test2@example.com");
        testUsers = Arrays.asList(user1, user2);
    }

    @Test
    @DisplayName("Test getAllUsers returns list of users")
    void testGetAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(testUsers);
        
        mockMvc.perform(get("/api/users/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("test1"))
                .andExpect(jsonPath("$[1].username").value("test2"));
    }

    @Test
    @DisplayName("Test getUserByUsername returns user when found")
    void testGetUserByUsername() throws Exception {
        User user1 = testUsers.get(0);
        when(userService.getUserByUsername("test1")).thenReturn(Optional.of(user1));
        
        mockMvc.perform(get("/api/users/by-username").param("username", "test1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("test1"))
                .andExpect(jsonPath("$.email").value("test1@example.com"));
    }

    @Test
    @DisplayName("Test getUserByUsername returns null when not found")
    void testGetUserByUsernameNotFound() throws Exception {
        when(userService.getUserByUsername("test3")).thenReturn(Optional.empty());
        
        mockMvc.perform(get("/api/users/by-username").param("username", "test3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    @DisplayName("Test getUserByEmail returns user when found")
    void testGetUserByEmail() throws Exception {
        User user1 = testUsers.get(0);
        when(userService.getUserByEmail("test1@example.com")).thenReturn(Optional.of(user1));
        
        mockMvc.perform(get("/api/users/by-email").param("email", "test1@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("test1"))
                .andExpect(jsonPath("$.email").value("test1@example.com"));
    }

    @Test
    @DisplayName("Test getUserByEmail returns null when not found")
    void testGetUserByEmailNotFound() throws Exception {
        when(userService.getUserByEmail("test3@example.com")).thenReturn(Optional.empty());
        
        mockMvc.perform(get("/api/users/by-email").param("email", "test3@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }
}