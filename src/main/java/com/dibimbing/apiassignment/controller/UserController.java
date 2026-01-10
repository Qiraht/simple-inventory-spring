package com.dibimbing.apiassignment.controller;

import com.dibimbing.apiassignment.dto.UserLoginReqDTO;
import com.dibimbing.apiassignment.dto.UserLoginResDTO;
import com.dibimbing.apiassignment.dto.UserRegisterReqDTO;
import com.dibimbing.apiassignment.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public String postRegister(@Valid @RequestBody UserRegisterReqDTO request) {
        return userService.registerUser(request);
    }

    @PostMapping("/login")
    public UserLoginResDTO postLogin(@Valid @RequestBody UserLoginReqDTO request) {
        return userService.LoginUser(request);
    }
}
