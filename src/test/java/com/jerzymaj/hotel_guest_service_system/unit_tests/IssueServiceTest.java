package com.jerzymaj.hotel_guest_service_system.unit_tests;

import com.jerzymaj.hotel_guest_service_system.DTOs.IssueCreateRequestDto;
import com.jerzymaj.hotel_guest_service_system.enums.IssueStatus;
import com.jerzymaj.hotel_guest_service_system.enums.IssueType;
import com.jerzymaj.hotel_guest_service_system.enums.PreferredTimeOption;
import com.jerzymaj.hotel_guest_service_system.models.Issue;
import com.jerzymaj.hotel_guest_service_system.models.User;
import com.jerzymaj.hotel_guest_service_system.repositories.IssueRepository;
import com.jerzymaj.hotel_guest_service_system.repositories.UserRepository;
import com.jerzymaj.hotel_guest_service_system.security.AuthenticationFacadeImpl;
import com.jerzymaj.hotel_guest_service_system.services.EmailService;
import com.jerzymaj.hotel_guest_service_system.services.IssueService;
import com.jerzymaj.hotel_guest_service_system.services.StorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IssueServiceTest {

    @Mock
    private IssueRepository issueRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationFacadeImpl authenticationFacade;

    @Mock
    private StorageService storageService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private IssueService issueService;

    @Test
    public void createIssueByUserId_IfSuccess() throws IOException {
        String email = "test@gmail.com";
        String title = "title";
        String description = "description";

        MultipartFile photo = new MockMultipartFile("photo", "test.jpg", "image/jpeg",
                "content".getBytes());

        IssueCreateRequestDto issueCreateRequestDto = new IssueCreateRequestDto(
                IssueType.RECEPTION,
                title,
                description,
                101,
                null,
                PreferredTimeOption.AS_SOON_AS_POSSIBLE,
                null,
                null
        );

        User user = User.builder()
                .id(1L)
                .email(email)
                .build();

        when(authenticationFacade.getAuthenticatedUserEmail()).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(issueRepository.save(any(Issue.class))).thenAnswer(i -> i.getArguments()[0]);
        when(storageService.savePhoto(photo)).thenReturn("test.jpg");
        doNothing().when(emailService).sendNotificationEmail(user, title,description);

        Issue actualResult = issueService.createIssue(photo, issueCreateRequestDto);

        assertThat(actualResult.getTitle()).isEqualTo("title");
        assertThat(actualResult.getUser().getEmail()).isEqualTo(email);
        assertThat(actualResult.getPhotoPath()).contains("test.jpg");
        verify(issueRepository).save(any(Issue.class));
        verify(storageService).savePhoto(photo);
    }

    @Test
    public void findAllIssuesForAuthenticatedUser_IfSuccess() {
        String email = "test@gmail.com";

        Issue issue1 = Issue.builder().id(1L).title("title1").build();
        Issue issue2 = Issue.builder().id(2L).title("title2").build();
        List<Issue> expectedIssues = List.of(issue1, issue2);

        when(authenticationFacade.getAuthenticatedUserEmail()).thenReturn(email);
        when(issueRepository.findAllByUserEmailSortedByDate(email)).thenReturn(expectedIssues);

        List<Issue> actualResult = issueService.findAllIssuesForAuthenticatedUser();

        assertThat(actualResult)
                .hasSize(2)
                .containsExactly(issue1, issue2);

        verify(issueRepository).findAllByUserEmailSortedByDate(email);
    }

    @Test
    public void updateIssueStatus_IfSuccess() {
        Long issueId = 1L;

        Issue issue = Issue.builder().id(issueId).status(IssueStatus.NEW).build();

        when(issueRepository.findById(issueId)).thenReturn(Optional.of(issue));

        issueService.updateIssueStatus(issueId, IssueStatus.OPEN);

        assertThat(issue.getStatus()).isEqualTo(IssueStatus.OPEN);
        verify(issueRepository).findById(issueId);
    }
}

