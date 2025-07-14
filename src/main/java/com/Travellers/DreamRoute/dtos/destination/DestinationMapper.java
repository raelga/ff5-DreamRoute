package com.Travellers.DreamRoute.dtos.destination;

import com.Travellers.DreamRoute.models.Destination;
import com.Travellers.DreamRoute.models.User;

public interface DestinationMapper {
    Destination dtoToEntity(DestinationRequest dto, User user);
    DestinationResponse entityToDto(Destination destination);
}
