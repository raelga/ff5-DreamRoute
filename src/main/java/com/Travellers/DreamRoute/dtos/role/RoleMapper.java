package com.Travellers.DreamRoute.dtos.role;

import com.Travellers.DreamRoute.models.Role;

public interface RoleMapper {
    Role dtoToEntity(RoleRequest request);
    RoleResponse entityToDto(Role role);
}
