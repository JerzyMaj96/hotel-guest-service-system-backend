package com.jerzymaj.hotel_guest_service_system.integration_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jerzymaj.hotel_guest_service_system.DTOs.IssueCreateRequestDto;
import com.jerzymaj.hotel_guest_service_system.enums.IssueType;
import com.jerzymaj.hotel_guest_service_system.enums.PreferredTimeOption;
import com.jerzymaj.hotel_guest_service_system.enums.UserType;
import com.jerzymaj.hotel_guest_service_system.models.User;
import com.jerzymaj.hotel_guest_service_system.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
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

    private User user;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .firstName("Paweł")
                .lastName("Kowalski")
                .email("pawel@gmail.com")
                .password(passwordEncoder.encode("secret123"))
                .userType(UserType.GUEST)
                .build();

        userRepository.save(user);
    }

    @Test
    @WithMockUser(username = "pawel@gmail.com", roles = "GUEST")
    public void createIssue() throws Exception {

        IssueCreateRequestDto issueCreateRequestDto = new IssueCreateRequestDto(IssueType.RECEPTION, "problem",
                "description", 101, "photoPath", PreferredTimeOption.AS_SOON_AS_POSSIBLE, null, null);

        mockMvc.perform(post("/hgss/api/issues")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(issueCreateRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value(IssueType.RECEPTION.name()))
                .andExpect(jsonPath("$.title").value(issueCreateRequestDto.title()))
                .andExpect(jsonPath("$.roomNumber").value(issueCreateRequestDto.roomNumber()));
    }

    @Test
    @WithMockUser(username = "pawel@gmail.com", roles = "GUEST")
    public void findAllIssuesByUserId() throws Exception {

        mockMvc.perform(get("/hgss/api/issues")
                        .param("userId", user.getId().toString())
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }
}
