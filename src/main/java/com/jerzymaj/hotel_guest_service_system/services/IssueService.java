package com.jerzymaj.hotel_guest_service_system.services;

import com.jerzymaj.hotel_guest_service_system.DTOs.IssueCreateRequestDto;
import com.jerzymaj.hotel_guest_service_system.DTOs.Notification;
import com.jerzymaj.hotel_guest_service_system.enums.IssueStatus;
import com.jerzymaj.hotel_guest_service_system.exceptions.IssueNotFoundException;
import com.jerzymaj.hotel_guest_service_system.exceptions.UserNotFoundException;
import com.jerzymaj.hotel_guest_service_system.models.Issue;
import com.jerzymaj.hotel_guest_service_system.models.User;
import com.jerzymaj.hotel_guest_service_system.repositories.IssueRepository;
import com.jerzymaj.hotel_guest_service_system.repositories.UserRepository;
import com.jerzymaj.hotel_guest_service_system.security.AuthenticationFacade;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IssueService {

    private final IssueRepository issueRepository;
    private final UserRepository userRepository;
    private final AuthenticationFacade authenticationFacade;
    private final NotificationSenderFactory notificationSenderFactory;
    private final PhotoStorage photoStorage;

    @Value("${app.support.email}")
    private String techEmail;

    @Transactional
    public Issue createIssue(MultipartFile photo, IssueCreateRequestDto issueCreateRequestDto) {

        User user = getAuthenticatedUser();
        String fileName = null;

        if (photo != null && !photo.isEmpty()) {
            fileName = photoStorage.savePhoto(photo);
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

        notificationSenderFactory.getFor(user.getEmail()).send(new Notification(user.getEmail(), "Issue created",
                "Your issue '" + issue.getTitle() + "' has been created and is being reviewed by our technical support team.", techEmail));

        return savedIssue;
    }

    public List<Issue> findAllIssuesForAuthenticatedUser() {
        String email = authenticationFacade.getAuthenticatedUserEmail();

        return issueRepository.findAllByUserEmailSortedByDate(email);
    }

    @PreAuthorize("hasRole('TECHNICAL_SUPPORT')")
    public List<Issue> findAllIssues() {
        return issueRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    @PreAuthorize("hasRole('TECHNICAL_SUPPORT')")
    public void updateIssueStatus(Long issueId, IssueStatus issueStatus) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new IssueNotFoundException("Issue with id " + issueId + " not found"));

        issue.setStatus(issueStatus);

        notificationSenderFactory.getFor(issue.getUser().getEmail()).send(new Notification(issue.getUser().getEmail(), "Issue status updated",
                "The status of your issue '" + issue.getTitle() + "' has been updated to: " + issueStatus, techEmail));

        if (issue.getUser().getPhoneNumber() != null) {
            notificationSenderFactory.getFor(issue.getUser().getPhoneNumber()).send(new Notification(issue.getUser().getPhoneNumber(), "Issue status updated",
                    "The status of your issue '" + issue.getTitle() + "' has been updated to: " + issueStatus));
        }
    }

    public Resource getPhoto(String fileName) throws MalformedURLException {
        Path filePath = Paths.get("upload-dir").toAbsolutePath().resolve(fileName);
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() && !resource.isReadable()) {
            throw new RuntimeException("Could not read file: " + fileName);
        }

        return resource;
    }

    private User getAuthenticatedUser() {
        String email = authenticationFacade.getAuthenticatedUserEmail();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found"));
    }
}
