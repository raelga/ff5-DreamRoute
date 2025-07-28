package com.Travellers.DreamRoute.services;

import com.Travellers.DreamRoute.dtos.role.RoleMapperImpl;
import com.Travellers.DreamRoute.dtos.role.RoleResponse;
import com.Travellers.DreamRoute.exceptions.EntityNotFoundException;
import com.Travellers.DreamRoute.models.Role;
import com.Travellers.DreamRoute.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapperImpl roleMapperImpl;

    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(role -> roleMapperImpl.entityToDto(role))
                .collect(Collectors.toList());
    }

    public RoleResponse getRoleById(Long id){
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Role.class.getSimpleName(), id));
        return roleMapperImpl.entityToDto(role);
    }
}
