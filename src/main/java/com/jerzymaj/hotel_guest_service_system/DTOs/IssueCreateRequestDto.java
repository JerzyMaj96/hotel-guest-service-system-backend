package com.jerzymaj.hotel_guest_service_system.DTOs;

import com.jerzymaj.hotel_guest_service_system.enums.IssueType;
import com.jerzymaj.hotel_guest_service_system.enums.PreferredTimeOption;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record IssueCreateRequestDto(@NotNull IssueType type,
                                    @NotBlank String title,
                                    @NotBlank String description,
                                    @NotNull int roomNumber,
                                    String photoPath,
                                    @NotNull PreferredTimeOption preferredTimeOption,
                                    LocalDate preferredDate,
                                    LocalTime preferredTime) {
}
