package com.jerzymaj.hotel_guest_service_system.services;

import com.jerzymaj.hotel_guest_service_system.DTOs.IssueCreateRequestDto;
import com.jerzymaj.hotel_guest_service_system.enums.IssueStatus;
import com.jerzymaj.hotel_guest_service_system.exceptions.UserNotFoundException;
import com.jerzymaj.hotel_guest_service_system.models.Issue;
import com.jerzymaj.hotel_guest_service_system.models.User;
import com.jerzymaj.hotel_guest_service_system.repositories.IssueRepository;
import com.jerzymaj.hotel_guest_service_system.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IssueService {

    private final IssueRepository issueRepository;
    private final UserRepository userRepository;

    @Transactional
    public Issue createIssue(IssueCreateRequestDto issueCreateRequestDto) {

        Long currentUserId = getCurrentUserId();

        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + currentUserId + " not found"));

        Issue issue = Issue.builder()
                .type(issueCreateRequestDto.type())
                .title(issueCreateRequestDto.title())
                .description(issueCreateRequestDto.description())
                .roomNumber(issueCreateRequestDto.roomNumber())
                .preferredTimeOption(issueCreateRequestDto.preferredTimeOption())
                .preferredDate(issueCreateRequestDto.preferredDate())
                .preferredTime(issueCreateRequestDto.preferredTime())
                .status(IssueStatus.NEW)
                .user(user)
                .build();

        return issueRepository.save(issue);
    }

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found"))
                .getId();
    }
}
