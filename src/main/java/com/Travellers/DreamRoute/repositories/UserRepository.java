package com.Travellers.DreamRoute.repositories;

import com.Travellers.DreamRoute.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
