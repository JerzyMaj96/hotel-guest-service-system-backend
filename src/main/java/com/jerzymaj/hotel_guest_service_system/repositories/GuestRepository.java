package com.jerzymaj.hotel_guest_service_system.repositories;

import com.jerzymaj.hotel_guest_service_system.models.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GuestRepository extends JpaRepository<Guest, Long> {

    Optional<Guest> findByName(String name);
}
