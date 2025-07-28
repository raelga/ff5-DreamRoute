package com.Travellers.DreamRoute.dtos.user;

import java.util.List;

public record UserResponse(
        Long id,
        String username,
        String email,
        List<String> destinations,
        List<String> roles
) {
}