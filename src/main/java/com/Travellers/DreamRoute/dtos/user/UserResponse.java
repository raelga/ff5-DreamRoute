package com.Travellers.DreamRoute.dtos.user;

import java.util.List;

public record UserResponse(
        Long id,
        String username,
        String email,
        String password,
        List<String> destinations,
        List<String> roles
) {
}