package com.Travellers.DreamRoute.dtos.destination;

import com.Travellers.DreamRoute.models.Destination;
import com.Travellers.DreamRoute.models.User;
import org.springframework.stereotype.Component;

@Component
public class DestinationMapperImpl implements DestinationMapper{
    @Override
    public Destination dtoToEntity(DestinationRequest dto, User user) {
        return Destination.builder()
                .country(dto.country())
                .city(dto.city())
                .description(dto.description())
                .image(dto.image())
                .user(user)
                .build();
    }

    @Override
    public DestinationResponse entityToDto(Destination destination) {
        String username = (destination.getUser() != null) ? destination.getUser().getUsername() : null;
        return new DestinationResponse(
                destination.getId(),
                destination.getCountry(),
                destination.getCity(),
                destination.getDescription(),
                destination.getImage(),
                username
        );
    }
}