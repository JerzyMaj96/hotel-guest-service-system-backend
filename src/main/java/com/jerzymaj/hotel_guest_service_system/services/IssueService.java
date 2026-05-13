package com.jerzymaj.hotel_guest_service_system.services;

import com.jerzymaj.hotel_guest_service_system.DTOs.IssueCreateRequestDto;
import com.jerzymaj.hotel_guest_service_system.enums.IssueStatus;
import com.jerzymaj.hotel_guest_service_system.exceptions.IssueNotFoundException;
import com.jerzymaj.hotel_guest_service_system.exceptions.PhotoStorageException;
import com.jerzymaj.hotel_guest_service_system.exceptions.UserNotFoundException;
import com.jerzymaj.hotel_guest_service_system.models.Issue;
import com.jerzymaj.hotel_guest_service_system.models.User;
import com.jerzymaj.hotel_guest_service_system.repositories.IssueRepository;
import com.jerzymaj.hotel_guest_service_system.repositories.UserRepository;
import com.jerzymaj.hotel_guest_service_system.security.AuthenticationFacade;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IssueService {

    private final IssueRepository issueRepository;
    private final UserRepository userRepository;
    private final AuthenticationFacade authenticationFacade;
    private final EmailService emailService;
    private final PhotoStorageService photoStorageService;

    @Transactional
    public Issue createIssue(MultipartFile photo, IssueCreateRequestDto issueCreateRequestDto) {

        User user = getAuthenticatedUser();

        String fileName = null;

        if (photo != null && !photo.isEmpty()) {
            try {
                fileName = photoStorageService.savePhoto(photo);
            } catch (IOException ex) {
                throw new PhotoStorageException("Failed to save photo", ex);
            }
        }

        Issue issue = Issue.builder()
                .type(issueCreateRequestDto.type())
                .title(issueCreateRequestDto.title())
                .description(issueCreateRequestDto.description())
                .roomNumber(issueCreateRequestDto.roomNumber())
                .photoPath(fileName)
                .preferredTimeOption(issueCreateRequestDto.preferredTimeOption())
                .preferredDate(issueCreateRequestDto.preferredDate())
                .preferredTime(issueCreateRequestDto.preferredTime())
                .user(user)
                .build();

        Issue savedIssue = issueRepository.save(issue);

        emailService.sendNotificationEmail(user, issueCreateRequestDto.title(), issueCreateRequestDto.description());

        return savedIssue;
    }

    public List<Issue> findAllIssuesForAuthenticatedUser() {
        String email = authenticationFacade.getAuthenticatedUserEmail();

        return issueRepository.findAllByUserEmailSortedByDate(email);
    }

    @Transactional
    @PreAuthorize("hasRole('TECHNICAL_SUPPORT')")
    public void updateIssueStatus(Long issueId, IssueStatus issueStatus) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new IssueNotFoundException("Issue with id " + issueId + " not found"));

        issue.setStatus(issueStatus);
    }

    private User getAuthenticatedUser() {
        String email = authenticationFacade.getAuthenticatedUserEmail();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found"));
    }
}
