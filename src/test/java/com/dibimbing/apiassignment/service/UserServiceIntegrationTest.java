package com.dibimbing.apiassignment.service;

import com.dibimbing.apiassignment.dto.UserLoginReqDTO;
import com.dibimbing.apiassignment.dto.UserLoginResDTO;
import com.dibimbing.apiassignment.dto.UserRegisterReqDTO;
import com.dibimbing.apiassignment.entity.Role;
import com.dibimbing.apiassignment.entity.User;
import com.dibimbing.apiassignment.exceptions.custom.NotFoundException;
import com.dibimbing.apiassignment.exceptions.custom.ValidationException;
import com.dibimbing.apiassignment.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void registerUser_ShouldPersistUser() {
        UserRegisterReqDTO request = new UserRegisterReqDTO("newuser", "password123", "newuser@test.com");

        String result = userService.registerUser(request);

        assertEquals("Register successfully", result);

        Optional<User> saved = userRepository.findByUsername("newuser");
        assertTrue(saved.isPresent());
        assertEquals("newuser@test.com", saved.get().getEmail());
        assertEquals(Role.USER, saved.get().getRole());
        // Password should be encoded (not plain text)
        assertNotEquals("password123", saved.get().getPassword());
    }

    @Test
    void loginUser_ShouldReturnToken() {
        // First register a user
        UserRegisterReqDTO registerReq = new UserRegisterReqDTO("loginuser", "password123", "login@test.com");
        userService.registerUser(registerReq);

        // Then login
        UserLoginReqDTO loginReq = new UserLoginReqDTO("loginuser", "password123");
        UserLoginResDTO result = userService.LoginUser(loginReq);

        assertNotNull(result);
        assertNotNull(result.getToken());
        assertFalse(result.getToken().isEmpty());
    }

    @Test
    void loginUser_ShouldThrow_WhenWrongPassword() {
        // Register user first
        UserRegisterReqDTO registerReq = new UserRegisterReqDTO("wrongpw", "password123", "wrongpw@test.com");
        userService.registerUser(registerReq);

        // Try login with wrong password
        UserLoginReqDTO loginReq = new UserLoginReqDTO("wrongpw", "wrongpassword");

        assertThrows(ValidationException.class, () -> userService.LoginUser(loginReq));
    }

    @Test
    void loginUser_ShouldThrow_WhenUserNotFound() {
        UserLoginReqDTO loginReq = new UserLoginReqDTO("nonexistent", "password123");

        assertThrows(NotFoundException.class, () -> userService.LoginUser(loginReq));
    }
}
