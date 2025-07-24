package com.Travellers.DreamRoute.services;

import com.Travellers.DreamRoute.dtos.destination.DestinationMapperImpl;
import com.Travellers.DreamRoute.dtos.destination.DestinationResponse;
import com.Travellers.DreamRoute.dtos.user.UserMapperImpl;
import com.Travellers.DreamRoute.dtos.user.UserRequest;
import com.Travellers.DreamRoute.dtos.user.UserResponse;
import com.Travellers.DreamRoute.exceptions.EntityNotFoundException;
import com.Travellers.DreamRoute.models.Destination;
import com.Travellers.DreamRoute.models.Role;
import com.Travellers.DreamRoute.models.User;
import com.Travellers.DreamRoute.repositories.DestinationRepository;
import com.Travellers.DreamRoute.repositories.RoleRepository;
import com.Travellers.DreamRoute.repositories.UserRepository;
import com.Travellers.DreamRoute.security.UserDetail;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserMapperImpl userMapperImpl;
    private final RoleRepository roleRepository;
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

    @Transactional
    public UserResponse addUser(UserRequest userRequest) {
        Optional<User> user = userRepository.findByUsernameIgnoreCase(userRequest.username());
        if (user.isPresent()){
            throw new RuntimeException("User already exists with username: " + userRequest.username());
        }
        Optional<User> email = userRepository.findByEmailIgnoreCase((userRequest.email()));
        if (email.isPresent()){
            throw new RuntimeException("Email is already registered: " + userRequest.email());
        }
        List<Destination> initialListOfDestinations = List.of();
        Role userRole = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(()->new EntityNotFoundException("Role", "USER"));
        List<Role> roles = List.of(userRole);
        User newUser = userMapperImpl.dtoToEntity(userRequest, initialListOfDestinations, roles);
        newUser.setPassword(passwordEncoder.encode(userRequest.password()));
        User savedUser = userRepository.save(newUser);
        return userMapperImpl.entityToDto(savedUser);
    }

    public UserResponse updateUser(Long id, UserRequest userRequest, UserDetail userDetail){
        boolean isAdmin = userDetail.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !userDetail.getId().equals(id)){
            throw new AccessDeniedException("You don't have permission to update a user");
        }
        User user = userRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException(User.class.getSimpleName(), id));
        user.setUsername(userRequest.username());
        user.setEmail(userRequest.email());
        user.setPassword(passwordEncoder.encode(userRequest.password()));

        return userMapperImpl.entityToDto(userRepository.save(user));
    }

    public String deleteUser(Long id, UserDetail userDetail) {
        boolean isAdmin = userDetail.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !userDetail.getId().equals(id)){
            throw new AccessDeniedException("You don't have permission to delete a user");
        }
        User userToDelete = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(User.class.getSimpleName(), id));
        userRepository.delete(userToDelete);
        return "User with id " + id + " has been deleted";
    }

    @Override
    public UserDetail loadUserByUsername(String username) throws EntityNotFoundException {
        User user =  userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new EntityNotFoundException(User.class.getSimpleName(), username));
        return new UserDetail(user);
    }

}
