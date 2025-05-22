package com.example.tj.DisasterManagementPortal.controller;

import com.example.tj.DisasterManagementPortal.model.User;
import com.example.tj.DisasterManagementPortal.model.LoginRequest;
import com.example.tj.DisasterManagementPortal.model.UserSignupRequest;
import com.example.tj.DisasterManagementPortal.service.UserService;
import com.example.tj.DisasterManagementPortal.service.UserServiceImpl;
import com.example.tj.DisasterManagementPortal.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserController userController;  // Inject the mock dependencies into the controller

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Initialize mocks before each test
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();  // Set up the mock MVC with the controller
    }

    // Test: Get All Users
    @Test
    void testGetAllUsers() throws Exception {
        User user1 = new User();
        User user2 = new User();
        when(userService.getAllUsers()).thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));  // Expecting 2 users in the response
    }

    // Test: Get User by ID - Success
    @Test
    void testGetUserByIdSuccess() throws Exception {
        User user = new User();
        user.setId(1L);
        when(userService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));  // Expecting user ID to be 1 in the response
    }

    // Test: Get User by ID - Not Found
    @Test
    void testGetUserByIdNotFound() throws Exception {
        when(userService.getUserById(1L)).thenReturn(null);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isNotFound());  // Expecting 404 status for a non-existing user
    }

    // Test: Create User - Success
    @Test
    void testCreateUserSuccess() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");
        when(userService.createUser(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));  // Expecting the user's email in the response
    }

    // Test: Update User - Success
    @Test
    void testUpdateUserSuccess() throws Exception {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setName("Old Name");

        User updateUser = new User();
        updateUser.setName("Updated Name");

        when(userService.getUserById(1L)).thenReturn(existingUser);
        when(userService.updateUser(any(User.class))).thenReturn(updateUser);

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));  // Expecting updated name in the response
    }

    // Test: Delete User
    @Test
    void testDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());  // Expecting 200 OK status on successful deletion
    }

    // Test: Get User by Email - Success
    @Test
    void testGetUserByEmailSuccess() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");
        when(userService.getUserByEmail("test@example.com")).thenReturn(user);

        mockMvc.perform(get("/users/email/test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));  // Expecting the user's email in the response
    }

    // Test: User Signup - Failure (Password Validation)
    @Test
    void testUserSignupPasswordValidationFailure() throws Exception {
        UserSignupRequest signupRequest = new UserSignupRequest();
        signupRequest.setEmail("newuser@example.com");
        signupRequest.setPassword("short");
        signupRequest.setConfirmPassword("short");
        signupRequest.setName("New User");
        signupRequest.setCity("City");
        signupRequest.setCountry("Country");

        mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Password must be at least 8 characters long and include at least one number and one special character."));
    }
}
