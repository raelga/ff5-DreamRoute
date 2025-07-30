package com.Travellers.DreamRoute.repositories;

import com.Travellers.DreamRoute.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleNameIgnoreCase(String roleName);
}