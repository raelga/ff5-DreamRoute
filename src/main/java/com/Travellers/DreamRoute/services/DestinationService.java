package com.Travellers.DreamRoute.services;

import com.Travellers.DreamRoute.dtos.destination.DestinationMapperImpl;
import com.Travellers.DreamRoute.dtos.destination.DestinationRequest;
import com.Travellers.DreamRoute.dtos.destination.DestinationResponse;
import com.Travellers.DreamRoute.dtos.user.UserMapperImpl;
import com.Travellers.DreamRoute.exceptions.EntityNotFoundException;
import com.Travellers.DreamRoute.models.Destination;
import com.Travellers.DreamRoute.models.User;
import com.Travellers.DreamRoute.repositories.DestinationRepository;
import com.Travellers.DreamRoute.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class DestinationService {
    private final DestinationRepository destinationRepository;
    private final DestinationMapperImpl destinationMapperImpl;
    private final UserRepository userRepository;

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

    public List<DestinationResponse> getDestinationsByUserId(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("User not found with id " + id));
        List<Destination> destinations = destinationRepository.findAllByUser(user);
        return destinations.stream()
                .map(destination -> destinationMapperImpl.entityToDto(destination))
                .toList();
    }

    public DestinationResponse addDestination(DestinationRequest destinationRequest, String username){
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new NoSuchElementException("user not found"));
        Destination destination = destinationMapperImpl.dtoToEntity(destinationRequest, user);

        destinationRepository.save(destination);
        return destinationMapperImpl.entityToDto(destination);
    }

    public DestinationResponse updateDestination(Long id, DestinationRequest destinationRequest) {
        Destination destinationToUpdate = destinationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Destination.class.getSimpleName(), id));

        destinationToUpdate.setCountry(destinationRequest.country());
        destinationToUpdate.setCity(destinationRequest.city());
        destinationToUpdate.setDescription(destinationRequest.description());
        destinationToUpdate.setImage(destinationRequest.image());

        Destination updatedDestination = destinationRepository.save(destinationToUpdate);

        return destinationMapperImpl.entityToDto(updatedDestination);
    }

    public void deleteDestination(Long id) {
        Destination destinationToDelete = destinationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Destination.class.getSimpleName(), id));
        destinationRepository.delete(destinationToDelete);
    }
}