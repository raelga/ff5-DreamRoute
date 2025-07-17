package com.Travellers.DreamRoute.repositories;

import com.Travellers.DreamRoute.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsernameIgnoreCase(String username);
    Optional<User> findByUsername(String username);
}
