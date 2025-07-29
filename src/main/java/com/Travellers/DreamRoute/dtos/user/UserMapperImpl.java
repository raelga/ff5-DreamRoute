package com.Travellers.DreamRoute.dtos.user;

import com.Travellers.DreamRoute.models.Destination;
import com.Travellers.DreamRoute.models.Role;
import com.Travellers.DreamRoute.models.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserMapperImpl implements UserMapper {
    @Override
    public User dtoToEntity(UserRequest dto, List<Destination> destinations, List<Role> roles) {
        return User.builder()
                .username(dto.username())
                .email(dto.email())
                .password(dto.password())
                .destinations(new ArrayList<>(destinations))
                .roles(new ArrayList<>(roles))
                .build();
    }

    @Override
    public UserResponse entityToDto(User user) {
        List<String> destinations = user.getDestinations().stream()
                .map(destination -> destination.getCity())
                .toList();
        List<String> roles = user.getRoles().stream()
                .map(role -> role.getRoleName())
                .toList();
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                destinations,
                roles
        );
    }
}
