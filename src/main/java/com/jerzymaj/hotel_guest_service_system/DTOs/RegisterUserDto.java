package com.jerzymaj.hotel_guest_service_system.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserDto(@NotBlank String firstName,@NotBlank String lastName, @NotBlank @Size(min = 8) String password,
                              @Email @NotBlank  String email) {
}
