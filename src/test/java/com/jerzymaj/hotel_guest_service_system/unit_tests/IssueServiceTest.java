package com.jerzymaj.hotel_guest_service_system.unit_tests;

import com.jerzymaj.hotel_guest_service_system.DTOs.IssueCreateRequestDto;
import com.jerzymaj.hotel_guest_service_system.enums.IssueType;
import com.jerzymaj.hotel_guest_service_system.enums.PreferredTimeOption;
import com.jerzymaj.hotel_guest_service_system.models.Issue;
import com.jerzymaj.hotel_guest_service_system.models.User;
import com.jerzymaj.hotel_guest_service_system.repositories.IssueRepository;
import com.jerzymaj.hotel_guest_service_system.repositories.UserRepository;
import com.jerzymaj.hotel_guest_service_system.security.AuthenticationFacade;
import com.jerzymaj.hotel_guest_service_system.services.IssueService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @InjectMocks
    private IssueService issueService;

    @Test
    public void createIssueByUserId_IfSuccess() {
        String email = "test@gmail.com";

        IssueCreateRequestDto issueCreateRequestDto = new IssueCreateRequestDto(
                IssueType.RECEPTION,
                "title",
                "description",
                101,
                "this/is/photo/path",
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

        Issue actualResult = issueService.createIssue(issueCreateRequestDto);

        assertThat(actualResult.getTitle()).isEqualTo("title");
        assertThat(actualResult.getUser().getEmail()).isEqualTo(email);
        verify(issueRepository).save(any(Issue.class));
    }
}

