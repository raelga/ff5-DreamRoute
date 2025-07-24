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

import org.springframework.security.access.AccessDeniedException;
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

    private void checkOwnership(Destination destination, UserDetail userDetails) {
        if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return;
        }
        if (!userDetails.getUsername().equals(destination.getUser().getUsername())) {
            throw new AccessDeniedException("You are not authorized to perform this action on this destination.");
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

    @Transactional
    public DestinationResponse updateDestination(Long id, DestinationRequest destinationRequest, UserDetail userDetails) {
        Destination destinationToUpdate = destinationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Destination.class.getSimpleName(), id));

        checkOwnership(destinationToUpdate, userDetails);

        destinationToUpdate.setCountry(destinationRequest.country());
        destinationToUpdate.setCity(destinationRequest.city());
        destinationToUpdate.setDescription(destinationRequest.description());
        destinationToUpdate.setImage(destinationRequest.image());

        Destination updatedDestination = destinationRepository.save(destinationToUpdate);

        return destinationMapperImpl.entityToDto(updatedDestination);
    }

    @Transactional
    public String deleteDestination(Long id, UserDetail userDetails) {
        Destination destinationToDelete = destinationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Destination.class.getSimpleName(), id));

        checkOwnership(destinationToDelete, userDetails);

        destinationRepository.delete(destinationToDelete);
        return "Destination with id " + id + " has been deleted";
    }
}