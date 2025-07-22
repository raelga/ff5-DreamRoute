package com.Travellers.DreamRoute.services;

import com.Travellers.DreamRoute.dtos.destination.DestinationMapperImpl;
import com.Travellers.DreamRoute.dtos.user.UserMapperImpl;
import com.Travellers.DreamRoute.dtos.user.UserRequest;
import com.Travellers.DreamRoute.dtos.user.UserResponse;
import com.Travellers.DreamRoute.exceptions.EntityNotFoundException;
import com.Travellers.DreamRoute.models.Destination;
import com.Travellers.DreamRoute.models.User;
import com.Travellers.DreamRoute.repositories.DestinationRepository;
import com.Travellers.DreamRoute.repositories.UserRepository;
import com.Travellers.DreamRoute.security.UserDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserMapperImpl userMapperImpl;
    private final DestinationRepository destinationRepository;
    private final DestinationMapperImpl destinationMapperImpl;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserResponse getUserByUsername(String username){
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(()-> new EntityNotFoundException(User.class.getSimpleName(), username));
        return userMapperImpl.entityToDto(user);
    }

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> userMapperImpl.entityToDto(user))
                .toList();
    }
    public UserResponse updateUser(String username, UserRequest userRequest){
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(()-> new EntityNotFoundException(User.class.getSimpleName(), username));
        user.setUsername(userRequest.username());
        user.setEmail(userRequest.email());
        user.setPassword(userRequest.password());

        return userMapperImpl.entityToDto(userRepository.save(user));
    }

    @Override
    public UserDetail loadUserByUsername(String username) throws EntityNotFoundException {
        User user =  userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new EntityNotFoundException(User.class.getSimpleName(), username));
        return new UserDetail(user);
    }
}
