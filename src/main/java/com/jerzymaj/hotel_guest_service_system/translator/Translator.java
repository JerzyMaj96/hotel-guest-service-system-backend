package com.jerzymaj.hotel_guest_service_system.translator;

import com.jerzymaj.hotel_guest_service_system.DTOs.IssueResponseDto;
import com.jerzymaj.hotel_guest_service_system.DTOs.UserDto;
import com.jerzymaj.hotel_guest_service_system.models.Issue;
import com.jerzymaj.hotel_guest_service_system.models.User;

public class Translator {

    public static UserDto convertUserToDto(User user) {
        return new UserDto(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getCreationDate());
    }

    public static IssueResponseDto convertIssueToDto(Issue issue) {
        return new IssueResponseDto(
                issue.getId(),
                issue.getType(),
                issue.getTitle(),
                issue.getStatus(),
                issue.getCreationDate(),
                issue.getRoomNumber()
        );
    }
}
