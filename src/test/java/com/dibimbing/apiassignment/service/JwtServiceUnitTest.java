package com.dibimbing.apiassignment.service;

import com.dibimbing.apiassignment.entity.Role;
import com.dibimbing.apiassignment.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceUnitTest {

    private JwtService jwtService;
    private User testUser;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setRole(Role.USER);
    }

    @Test
    void generateToken_ShouldReturnValidToken() {
        String token = jwtService.generateToken(testUser);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        String token = jwtService.generateToken(testUser);
        String username = jwtService.extractUsername(token);
        assertEquals(testUser.getUsername(), username);
    }

    @Test
    void isTokenValid_ShouldReturnTrueForValidToken() {
        String token = jwtService.generateToken(testUser);
        assertTrue(jwtService.isTokenValid(token, testUser));
    }

    @Test
    void isTokenValid_ShouldReturnFalseForDifferentUser() {
        String token = jwtService.generateToken(testUser);
        User anotherUser = new User();
        anotherUser.setUsername("anotheruser");
        assertFalse(jwtService.isTokenValid(token, anotherUser));
    }

    @Test
    void extractUsername_ShouldReturnNullForInvalidToken() {
        String invalidToken = "invalid.token.here";
        assertNull(jwtService.extractUsername(invalidToken));
    }
}
