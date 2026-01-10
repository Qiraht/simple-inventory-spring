package com.dibimbing.apiassignment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginReqDTO {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
