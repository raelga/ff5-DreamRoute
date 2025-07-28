package com.Travellers.DreamRoute.dtos.user;

import com.Travellers.DreamRoute.models.Destination;
import com.Travellers.DreamRoute.models.Role;
import com.Travellers.DreamRoute.models.User;

import java.util.List;

public interface UserMapper {
    User dtoToEntity(UserRequest dto, List<Destination> destinations, List<Role> roles);
    UserResponse entityToDto(User user);
}
