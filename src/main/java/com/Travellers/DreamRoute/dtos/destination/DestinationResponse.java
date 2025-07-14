package com.Travellers.DreamRoute.dtos.destination;

public record DestinationResponse(
        Long id,
        String country,
        String city,
        String description,
        String imageUrl,
        String username
) {
}