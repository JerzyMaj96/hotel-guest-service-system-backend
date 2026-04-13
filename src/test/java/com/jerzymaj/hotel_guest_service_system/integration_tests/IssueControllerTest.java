package com.jerzymaj.hotel_guest_service_system.integration_tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jerzymaj.hotel_guest_service_system.DTOs.IssueCreateRequestDto;
import com.jerzymaj.hotel_guest_service_system.enums.IssueType;
import com.jerzymaj.hotel_guest_service_system.enums.PreferredTimeOption;
import com.jerzymaj.hotel_guest_service_system.enums.UserType;
import com.jerzymaj.hotel_guest_service_system.models.User;
import com.jerzymaj.hotel_guest_service_system.repositories.UserRepository;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "management.health.mail.enabled=false")
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class IssueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private JavaMailSender javaMailSender;

    private User user;
    private IssueCreateRequestDto issueCreateRequestDto;
    private MockMultipartFile photoPart;
    private MockMultipartFile issuePart;

    @BeforeEach
    public void setUp() throws JsonProcessingException {
        user = User.builder()
                .firstName("Paweł")
                .lastName("Kowalski")
                .email("pawel@gmail.com")
                .password(passwordEncoder.encode("secret123"))
                .userType(UserType.GUEST)
                .build();

        userRepository.save(user);

        issueCreateRequestDto = new IssueCreateRequestDto(IssueType.RECEPTION, "problem",
                "description", 101, null, PreferredTimeOption.AS_SOON_AS_POSSIBLE, null, null);

        photoPart = new MockMultipartFile("photo", "test.txt", "image/jpeg",
                "content".getBytes());

        issuePart = new MockMultipartFile("issue", "", "application/json",
                objectMapper.writeValueAsBytes(issueCreateRequestDto));

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    @WithMockUser(username = "pawel@gmail.com", roles = "GUEST")
    public void createIssue() throws Exception {

        mockMvc.perform(multipart("/hgss/api/issues")
                        .file(photoPart)
                        .file(issuePart))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value(IssueType.RECEPTION.name()))
                .andExpect(jsonPath("$.title").value(issueCreateRequestDto.title()))
                .andExpect(jsonPath("$.roomNumber").value(issueCreateRequestDto.roomNumber()));
    }

    @Test
    @WithMockUser(username = "pawel@gmail.com", roles = "GUEST")
    public void findAllIssues() throws Exception {

        mockMvc.perform(get("/hgss/api/issues")
                        .param("userId", user.getId().toString())
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    public void updateIssueStatus() throws Exception {

        String response = mockMvc.perform(multipart("/hgss/api/issues")
                        .with(SecurityMockMvcRequestPostProcessors.user(user.getEmail()).roles(user.getUserType().name()))
                        .file(photoPart)
                        .file(issuePart))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long issueId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(patch("/hgss/api/issues/" + issueId + "/status")
                        .with(SecurityMockMvcRequestPostProcessors.user("tech@hotel.com").roles("TECHNICAL_SUPPORT"))
                        .param("issueStatus", "OPEN"))
                .andExpect(status().isNoContent());
    }
}
