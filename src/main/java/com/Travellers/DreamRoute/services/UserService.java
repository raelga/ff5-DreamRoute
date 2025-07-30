package com.Travellers.DreamRoute.services;

import com.Travellers.DreamRoute.dtos.destination.DestinationMapperImpl;
import com.Travellers.DreamRoute.dtos.destination.DestinationResponse;
import com.Travellers.DreamRoute.dtos.user.UserMapperImpl;
import com.Travellers.DreamRoute.dtos.user.UserRequest;
import com.Travellers.DreamRoute.dtos.user.UserResponse;
import com.Travellers.DreamRoute.dtos.user.UserUpdateRequest;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserMapperImpl userMapperImpl;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserResponse getUserByUsername(String username){
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(()-> new EntityNotFoundException(User.class.getSimpleName(), username));
        return userMapperImpl.entityToDto(user);
    }

    public UserResponse getUserById(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException(User.class.getSimpleName(), id));
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
        List<Destination> initialListOfDestinations = new ArrayList<>();
        Role userRole = roleRepository.findByRoleNameIgnoreCase("ROLE_USER")
                .orElseThrow(()->new EntityNotFoundException("Role", "USER"));
        List<Role> roles = new ArrayList<>();
        roles.add(userRole);
        User newUser = userMapperImpl.dtoToEntity(userRequest, initialListOfDestinations, roles);
        newUser.setPassword(passwordEncoder.encode(userRequest.password()));
        User savedUser = userRepository.save(newUser);
        return userMapperImpl.entityToDto(savedUser);
    }

    public UserResponse updateUser(Long id, UserUpdateRequest userRequest, UserDetail userDetail){
        User user = userRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException(User.class.getSimpleName(), id));

        boolean isAdmin = userDetail.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        boolean isOwner = Objects.equals(userDetail.getId(), id);

        if (!isAdmin && !isOwner){
            throw new AccessDeniedException("You don't have permission to update this user");
        }

        if (userRequest.username() != null && !userRequest.username().isBlank()) {
            if (!userRequest.username().equals(user.getUsername())) {
                if (userRepository.findByUsernameIgnoreCase(userRequest.username()).isPresent()) {
                    throw new IllegalArgumentException("Username already taken");
                }
                user.setUsername(userRequest.username());
            }
        }

        if (userRequest.email() != null && !userRequest.email().isBlank()) {
            if (!userRequest.email().equals(user.getEmail())) {
                if (userRepository.findByEmailIgnoreCase(userRequest.email()).isPresent()) {
                    throw new IllegalArgumentException("Email is already registered");
                }
                user.setEmail(userRequest.email());
            }
        }

        if (userRequest.password() != null && !userRequest.password().isBlank()) {
            if (isAdmin && !isOwner) {
                throw new AccessDeniedException("Admins are not allowed to change passwords of other users");
            }
            user.setPassword(passwordEncoder.encode(userRequest.password()));
        }

        if (userRequest.roles() != null && !userRequest.roles().isEmpty()) {
            if (!isAdmin) {
                throw new AccessDeniedException("Users are not allowed to change their own roles");
            }

            List<Role> updatedRoles = userRequest.roles().stream()
                    .map(roleName -> roleRepository.findByRoleNameIgnoreCase(roleName)
                            .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                    .collect(Collectors.toCollection(ArrayList::new));
            user.setRoles(updatedRoles);
        }

        User savedUser = userRepository.save(user);
        return userMapperImpl.entityToDto(savedUser);
    }

    public String deleteUser(Long id, UserDetail userDetail) {
        boolean isAdmin = userDetail.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin){
            throw new AccessDeniedException("Only administrators can delete users");
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

    @Transactional
    public UserResponse registerUser(UserRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username already taken");
        }

        if (userRepository.existsByEmail(request.email())){
            throw new IllegalArgumentException("Email already registered");
        }

        Role defaultRole = roleRepository.findByRoleNameIgnoreCase("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        List<Role> roles = new ArrayList<>();
        roles.add(defaultRole);
        User user = userMapperImpl.dtoToEntity(request, new ArrayList<>(), roles);

        user.setPassword(passwordEncoder.encode(request.password()));

        userRepository.save(user);
        return userMapperImpl.entityToDto(user);
    }
}