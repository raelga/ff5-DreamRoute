package com.Travellers.DreamRoute.dtos.destination;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record DestinationRequest(
        @NotBlank (message = "Country is required")
        @Size(max = 70, message = "Country must be less than 70 characters")
        String country,

        @NotBlank(message = "City is required")
        @Size(max = 70, message = "City must be less than 70 characters")
        String city,

        @NotBlank(message = "Description is required")
        @Size(max = 400, message = "Description must be less than 400 characters")
        String description,

        @NotBlank(message = "Image is required")
        @Pattern(message = "Must be a valid URL", regexp = "^(http|https)://.*$")
        String imageUrl
) {
}
