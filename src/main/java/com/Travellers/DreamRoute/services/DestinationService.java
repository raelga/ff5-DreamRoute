package com.Travellers.DreamRoute.services;

import com.Travellers.DreamRoute.dtos.destination.DestinationMapperImpl;
import com.Travellers.DreamRoute.dtos.destination.DestinationRequest;
import com.Travellers.DreamRoute.dtos.destination.DestinationResponse;
import com.Travellers.DreamRoute.exceptions.EntityNotFoundException;
import com.Travellers.DreamRoute.models.Destination;
import com.Travellers.DreamRoute.repositories.DestinationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DestinationService {
    private final DestinationRepository destinationRepository;
    private final DestinationMapperImpl destinationMapperImpl;

    public List<DestinationResponse> getAllDestinations() {
        List<Destination> destinations = destinationRepository.findAll();
        return destinations.stream()
                .map(destination -> destinationMapperImpl.entityToDto(destination))
                .toList();
    }

    public DestinationResponse getDestinationById(Long id) {
        Destination destination = destinationRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException(Destination.class.getSimpleName(), id));
        return destinationMapperImpl.entityToDto(destination);
    }

}
