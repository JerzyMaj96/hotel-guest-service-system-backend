package com.jerzymaj.hotel_guest_service_system.repositories;

import com.jerzymaj.hotel_guest_service_system.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
