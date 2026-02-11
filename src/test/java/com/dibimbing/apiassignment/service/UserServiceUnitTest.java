package com.dibimbing.apiassignment.service;

import com.dibimbing.apiassignment.dto.UserLoginReqDTO;
import com.dibimbing.apiassignment.dto.UserLoginResDTO;
import com.dibimbing.apiassignment.dto.UserRegisterReqDTO;
import com.dibimbing.apiassignment.entity.Role;
import com.dibimbing.apiassignment.entity.User;
import com.dibimbing.apiassignment.exceptions.custom.ValidationException;
import com.dibimbing.apiassignment.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private User user;
    private UserRegisterReqDTO userRegister;
    private UserLoginReqDTO userLogin;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("dibimbing");
        user.setPassword("encoded_password");
        user.setEmail("dibimbing@email");
        user.setRole(Role.USER);

        userRegister = new UserRegisterReqDTO();
        userRegister.setUsername("dibimbing");
        userRegister.setPassword("password");
        userRegister.setEmail("dibimbing@email");

        userLogin = new UserLoginReqDTO();
        userLogin.setUsername("dibimbing");
        userLogin.setPassword("password");
    }

    @Test
    void registerUser_successful() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");

        String result = userService.registerUser(userRegister);

        assertEquals("Register successfully", result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void LoginUser_successful() {
        when(userRepository.findByUsername("dibimbing")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encoded_password")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("mocked_token");

        UserLoginResDTO result = userService.LoginUser(userLogin);

        assertNotNull(result);
        assertEquals("mocked_token", result.getToken());
    }

    @Test
    void LoginUser_ShouldThrowException_WhenPasswordWrong() {
        when(userRepository.findByUsername("dibimbing")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encoded_password")).thenReturn(false);

        assertThrows(ValidationException.class, () -> userService.LoginUser(userLogin));
    }

    @Test
    void getUserByUsername_ShouldReturnUser() {
        when(userRepository.findByUsername("dibimbing")).thenReturn(Optional.of(user));

        User result = userService.getUserByUsername("dibimbing");

        assertEquals(user, result);
    }

    @Test
    void convertToUserDetails_ShouldWork() {
        UserDetails result = userService.convertToUserDetails(user);

        assertEquals(user.getUsername(), result.getUsername());
        assertTrue(result.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }
}
