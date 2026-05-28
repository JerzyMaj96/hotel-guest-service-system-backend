package com.jerzymaj.hotel_guest_service_system.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterUserDto(@NotBlank String firstName, @NotBlank String lastName, @NotBlank @Size(min = 8) String password,
                              @Email @NotBlank  String email,
                              @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Invalid phone number format. Use E.164 format (e.g. +48123456789)") String phoneNumber) {
}
