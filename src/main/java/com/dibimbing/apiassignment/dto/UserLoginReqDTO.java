package com.dibimbing.apiassignment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginReqDTO {
    @NotBlank(message = "Username is Required")
    @Size(min = 3, max = 50, message = "Username must be 3-50 characters")
    private String username;

    @NotBlank(message = "Password is Required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}
