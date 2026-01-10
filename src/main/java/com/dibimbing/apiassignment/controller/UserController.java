package com.dibimbing.apiassignment.controller;

import com.dibimbing.apiassignment.dto.UserLoginReqDTO;
import com.dibimbing.apiassignment.dto.UserLoginResDTO;
import com.dibimbing.apiassignment.dto.UserRegisterReqDTO;
import com.dibimbing.apiassignment.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> postRegister(
            @Valid @RequestBody UserRegisterReqDTO request) {
        String result = userService.registerUser(request);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResDTO> postLogin(@Valid @RequestBody UserLoginReqDTO request) {
        UserLoginResDTO result = userService.LoginUser(request);

        return ResponseEntity.ok(result);
    }
}
