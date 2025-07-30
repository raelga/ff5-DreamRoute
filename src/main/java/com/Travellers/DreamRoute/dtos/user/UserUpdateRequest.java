package com.Travellers.DreamRoute.dtos.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

public record UserUpdateRequest(
        @NotBlank (message = "Username is required")
        @Size(max = 20, message = "Username must be less than 20 characters")
        String username,

        @NotBlank(message = "Email is required")
        @Email(message = "Email not valid", regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
        String email,

        @Pattern(message = "Password must contain a minimum of 8 characters, including a number, one uppercase letter, one lowercase letter and one special character", regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=.])(?=\\S+$).{8,}$")
        String password,

        List<String> roles
) {
}