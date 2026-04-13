package com.jerzymaj.hotel_guest_service_system.unit_tests;

import com.jerzymaj.hotel_guest_service_system.DTOs.IssueCreateRequestDto;
import com.jerzymaj.hotel_guest_service_system.enums.IssueStatus;
import com.jerzymaj.hotel_guest_service_system.enums.IssueType;
import com.jerzymaj.hotel_guest_service_system.enums.PreferredTimeOption;
import com.jerzymaj.hotel_guest_service_system.models.Issue;
import com.jerzymaj.hotel_guest_service_system.models.User;
import com.jerzymaj.hotel_guest_service_system.repositories.IssueRepository;
import com.jerzymaj.hotel_guest_service_system.repositories.UserRepository;
import com.jerzymaj.hotel_guest_service_system.security.AuthenticationFacade;
import com.jerzymaj.hotel_guest_service_system.services.IssueService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

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
    private AuthenticationFacade authenticationFacade;

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private IssueService issueService;

    @Test
    public void createIssueByUserId_IfSuccess() throws MessagingException {
        String email = "test@gmail.com";

        MultipartFile photo = new MockMultipartFile("photo", "test.jpg", "image/jpeg",
                "content".getBytes());

        IssueCreateRequestDto issueCreateRequestDto = new IssueCreateRequestDto(
                IssueType.RECEPTION,
                "title",
                "description",
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

        MimeMessage message = mock(MimeMessage.class);

        when(authenticationFacade.getAuthenticatedUserEmail()).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(issueRepository.save(any(Issue.class))).thenAnswer(i -> i.getArguments()[0]);
        when(javaMailSender.createMimeMessage()).thenReturn(message);

        Issue actualResult = issueService.createIssue(photo, issueCreateRequestDto);

        assertThat(actualResult.getTitle()).isEqualTo("title");
        assertThat(actualResult.getUser().getEmail()).isEqualTo(email);
        assertThat(actualResult.getPhotoPath()).contains("test.jpg");
        verify(issueRepository).save(any(Issue.class));
        verify(javaMailSender).send(any(MimeMessage.class));
    }

    @Test
    public void findAllIssuesByUserId_IfSuccess() {
        Long userId = 1L;
        Issue issue1 = Issue.builder().id(1L).title("title1").build();
        Issue issue2 = Issue.builder().id(2L).title("title2").build();
        List<Issue> expectedIssues = List.of(issue1, issue2);

        when(issueRepository.findAllByUserIdSortedByDate(userId)).thenReturn(expectedIssues);

        List<Issue> actualResult = issueService.findAllIssuesByUserId(userId);

        assertThat(actualResult)
                .hasSize(2)
                .containsExactly(issue1, issue2);

        verify(issueRepository).findAllByUserIdSortedByDate(userId);
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

