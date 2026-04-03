package com.jerzymaj.hotel_guest_service_system.DTOs;

import java.time.LocalDateTime;

public record UserDto(Long id, String firstName, String lastName, String email, LocalDateTime creationDate) {
}
