package com.dibimbing.apiassignment.service;

import com.dibimbing.apiassignment.dto.UserLoginReqDTO;
import com.dibimbing.apiassignment.dto.UserLoginResDTO;
import com.dibimbing.apiassignment.dto.UserRegisterReqDTO;
import com.dibimbing.apiassignment.entity.Role;
import com.dibimbing.apiassignment.entity.User;
import com.dibimbing.apiassignment.exceptions.custom.NotFoundException;
import com.dibimbing.apiassignment.exceptions.custom.ValidationException;
import com.dibimbing.apiassignment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public String registerUser(UserRegisterReqDTO request) {
        // Username exist validation
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Username already exists: {}", request.getUsername());
            throw new ValidationException("Username already exists");
        }

        // TODO: Email validation

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole(Role.USER);
        userRepository.save(user);

        return "Register successfully";
    }

    public UserLoginResDTO LoginUser(UserLoginReqDTO request) {
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow(
                () -> new NotFoundException("Username not found")
        );

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Passwords don't match");
            throw new ValidationException("Passwords don't match");
        }

        String token = jwtService.generateToken(user);
        return new UserLoginResDTO(token);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new NotFoundException("User not found")
        );
    }

    public UserDetails convertToUserDetails(User user) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                // untuk method prePostEnabled securedEnabled jsr250Enabled
//                .roles(existUser.getAuthority())
//        prePostEnabled
                .authorities("ROLE_"+ user.getRole().getValue())
                .build();

    }
}
