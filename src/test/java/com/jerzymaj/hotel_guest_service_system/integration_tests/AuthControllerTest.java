package com.jerzymaj.hotel_guest_service_system.integration_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jerzymaj.hotel_guest_service_system.DTOs.LoginRequest;
import com.jerzymaj.hotel_guest_service_system.DTOs.RegisterUserDto;
import com.jerzymaj.hotel_guest_service_system.enums.UserType;
import com.jerzymaj.hotel_guest_service_system.models.User;
import com.jerzymaj.hotel_guest_service_system.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void registerUser() throws Exception {
        RegisterUserDto registerUserDto = new RegisterUserDto("Paweł", "Kowalski", "secret123", "pawel@gmail.com");

        mockMvc.perform(post("/hgss/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(registerUserDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value(registerUserDto.firstName()))
                .andExpect(jsonPath("$.lastName").value(registerUserDto.lastName()))
                .andExpect(jsonPath("$.email").value(registerUserDto.email()));
    }

    @Test
    public void loginUser() throws Exception {
        userRepository.deleteAll();

        User user = User.builder()
                .firstName("Paweł")
                .lastName("Kowalski")
                .email("pawel@gmail.com")
                .password(passwordEncoder.encode("secret123"))
                .userType(UserType.GUEST)
                .build();

        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest("pawel@gmail.com", "secret123");

        mockMvc.perform(post("/hgss/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString()).isNotEmpty());
    }
}
