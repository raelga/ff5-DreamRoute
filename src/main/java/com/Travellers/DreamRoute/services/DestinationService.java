package com.Travellers.DreamRoute.services;

import com.Travellers.DreamRoute.dtos.destination.DestinationMapperImpl;
import com.Travellers.DreamRoute.dtos.destination.DestinationRequest;
import com.Travellers.DreamRoute.dtos.destination.DestinationResponse;
import com.Travellers.DreamRoute.exceptions.EntityNotFoundException;
import com.Travellers.DreamRoute.models.Destination;
import com.Travellers.DreamRoute.models.User;
import com.Travellers.DreamRoute.repositories.DestinationRepository;
import com.Travellers.DreamRoute.repositories.UserRepository;
import com.Travellers.DreamRoute.security.UserDetail;
import jakarta.transaction.Transactional;
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

    private void validateUser(UserDetail userDetails) {
        if (userDetails == null || userDetails.getUsername() == null) {
            throw new IllegalArgumentException("User information is missing or invalid");
        }
    }

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

    @Transactional
    public DestinationResponse addDestination(DestinationRequest request, UserDetail userDetails) {
        validateUser(userDetails);

        User user = userRepository.findByUsernameIgnoreCase(userDetails.getUsername())
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        Destination destination = destinationMapperImpl.dtoToEntity(request, user);
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