package com.jerzymaj.hotel_guest_service_system.mappers;

import com.jerzymaj.hotel_guest_service_system.DTOs.IssueResponseDto;
import com.jerzymaj.hotel_guest_service_system.models.Issue;
import org.springframework.stereotype.Component;

@Component
public class IssueMapper {

    public static IssueResponseDto toDto(Issue issue) {
        return new IssueResponseDto(
                issue.getId(),
                issue.getType(),
                issue.getTitle(),
                issue.getPhotoPath(),
                issue.getStatus(),
                issue.getCreationDate(),
                issue.getRoomNumber()
        );
    }
}
