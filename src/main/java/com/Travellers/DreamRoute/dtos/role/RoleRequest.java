package com.Travellers.DreamRoute.dtos.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RoleRequest(
        @NotBlank(message = "Role name is required")
        @Size(min = 5, max = 20, message = "Role name must be between 5 and 20 characters")
        @Pattern(
                regexp = "^ROLE_[A-Z_]+$",
                message = "Role name must start with 'ROLE_' and contain only uppercase letters and underscores"
        )
        String roleName
) {
}
