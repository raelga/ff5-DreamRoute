package com.Travellers.DreamRoute.repositories;

import com.Travellers.DreamRoute.models.Destination;
import com.Travellers.DreamRoute.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DestinationRepository extends JpaRepository<Destination, Long> {
    List<Destination> findAllByUser(User user);
}