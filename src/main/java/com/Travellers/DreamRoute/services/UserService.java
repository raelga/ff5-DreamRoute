package com.Travellers.DreamRoute.services;

import com.Travellers.DreamRoute.dtos.destination.DestinationMapperImpl;
import com.Travellers.DreamRoute.dtos.user.UserMapperImpl;
import com.Travellers.DreamRoute.dtos.user.UserResponse;
import com.Travellers.DreamRoute.exceptions.EntityNotFoundException;
import com.Travellers.DreamRoute.models.User;
import com.Travellers.DreamRoute.repositories.DestinationRepository;
import com.Travellers.DreamRoute.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapperImpl userMapperImpl;
    private final DestinationRepository destinationRepository;
    private final DestinationMapperImpl destinationMapperImpl;

    public UserResponse getUserByUsername(String username){
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(()-> new EntityNotFoundException(User.class.getSimpleName(), username));
        return userMapperImpl.entityToDto(user);
    }
}
