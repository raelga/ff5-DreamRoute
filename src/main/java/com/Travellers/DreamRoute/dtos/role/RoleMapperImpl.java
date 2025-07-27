package com.Travellers.DreamRoute.dtos.role;

import com.Travellers.DreamRoute.models.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleMapperImpl implements RoleMapper{

    @Override
    public Role dtoToEntity(RoleRequest request){
        if (request == null) return null;

        return Role.builder()
                .roleName(request.roleName())
                .build();
    }

    @Override
    public RoleResponse entityToDto(Role role){
        if (role == null) return null;

        return new RoleResponse(
                role.getId(),
                role.getRoleName()
        );
    }
}
