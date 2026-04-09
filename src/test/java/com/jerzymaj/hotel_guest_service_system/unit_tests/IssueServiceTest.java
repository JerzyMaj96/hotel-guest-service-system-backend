package com.jerzymaj.hotel_guest_service_system.unit_tests;

import com.jerzymaj.hotel_guest_service_system.DTOs.IssueCreateRequestDto;
import com.jerzymaj.hotel_guest_service_system.enums.IssueType;
import com.jerzymaj.hotel_guest_service_system.enums.PreferredTimeOption;
import com.jerzymaj.hotel_guest_service_system.models.Issue;
import com.jerzymaj.hotel_guest_service_system.models.User;
import com.jerzymaj.hotel_guest_service_system.repositories.IssueRepository;
import com.jerzymaj.hotel_guest_service_system.repositories.UserRepository;
import com.jerzymaj.hotel_guest_service_system.services.IssueService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IssueServiceTest {

    @Mock
    private IssueRepository issueRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private IssueService issueService;

    @Test
    public void createIssueByUserId_IfSuccess() {
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
                .email("test@gmail.com")
                .build();

        try (MockedStatic<SecurityContextHolder> mockedSecurity = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            mockedSecurity.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("test@gmail.com");

            when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            when(issueRepository.save(any(Issue.class))).thenAnswer(invocation -> {
                Issue issue = invocation.getArgument(0);
                issue.setId(100L);
                return issue;
            });

            Issue actualResult = issueService.createIssue(issueCreateRequestDto);

            assertThat(actualResult).isNotNull();
            assertThat(actualResult.getId()).isEqualTo(100L);
            assertThat(actualResult.getTitle()).isEqualTo("title");
            assertThat(actualResult.getUser()).isEqualTo(user);

            verify(issueRepository).save(any(Issue.class));
        }
    }
}

