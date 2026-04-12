package com.jerzymaj.hotel_guest_service_system.DTOs;

import com.jerzymaj.hotel_guest_service_system.enums.IssueStatus;
import com.jerzymaj.hotel_guest_service_system.enums.IssueType;

import java.time.LocalDateTime;

public record IssueResponseDto(Long id,
                               IssueType type,
                               String title,
                               String photoUrl,
                               IssueStatus status,
                               LocalDateTime creationDate,
                               int roomNumber) {
}
