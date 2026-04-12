package com.jerzymaj.hotel_guest_service_system.services;

import com.jerzymaj.hotel_guest_service_system.DTOs.IssueCreateRequestDto;
import com.jerzymaj.hotel_guest_service_system.enums.IssueStatus;
import com.jerzymaj.hotel_guest_service_system.exceptions.IssueNotFoundException;
import com.jerzymaj.hotel_guest_service_system.exceptions.UserNotFoundException;
import com.jerzymaj.hotel_guest_service_system.models.Issue;
import com.jerzymaj.hotel_guest_service_system.models.User;
import com.jerzymaj.hotel_guest_service_system.repositories.IssueRepository;
import com.jerzymaj.hotel_guest_service_system.repositories.UserRepository;
import com.jerzymaj.hotel_guest_service_system.security.IAuthenticationFacade;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IssueService {

    private final IssueRepository issueRepository;
    private final UserRepository userRepository;
    private final IAuthenticationFacade authenticationFacade;
    private final JavaMailSender javaMailSender;

    @Transactional
    public Issue createIssue(IssueCreateRequestDto issueCreateRequestDto) throws MessagingException {

        String email = authenticationFacade.getAuthenticatedUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found"));

        Issue issue = Issue.builder()
                .type(issueCreateRequestDto.type())
                .title(issueCreateRequestDto.title())
                .description(issueCreateRequestDto.description())
                .roomNumber(issueCreateRequestDto.roomNumber())
                .photoPath(issueCreateRequestDto.photoPath())
                .preferredTimeOption(issueCreateRequestDto.preferredTimeOption())
                .preferredDate(issueCreateRequestDto.preferredDate())
                .preferredTime(issueCreateRequestDto.preferredTime())
                .status(IssueStatus.NEW)
                .user(user)
                .build();

        Issue savedIssue = issueRepository.save(issue);

        sendNotificationEmail(user, issueCreateRequestDto.title(), issueCreateRequestDto.description());

        return savedIssue;
    }

    private void sendNotificationEmail(User user, String title, String description) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(authenticationFacade.getAuthenticatedUserEmail());
        helper.setTo("tech@hotel.com");
        helper.setSubject(title + "-" + user.getFirstName() + " " + user.getLastName());
        helper.setText(description);

        javaMailSender.send(message);
    }

    public List<Issue> findAllIssuesByUserId(Long userId) {
        return issueRepository.findAllByUserIdSortedByDate(userId);
    }

    @Transactional
    @PreAuthorize("hasRole('TECHNICAL_SUPPORT')")
    public void updateIssueStatus(Long issueId, IssueStatus issueStatus) {


        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new IssueNotFoundException("Issue with id " + issueId + " not found"));

        issue.setStatus(issueStatus);
    }
}
