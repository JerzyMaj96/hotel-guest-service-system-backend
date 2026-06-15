package com.jerzymaj.hotel_guest_service_system.controllers;

import com.jerzymaj.hotel_guest_service_system.DTOs.LoginRequest;
import com.jerzymaj.hotel_guest_service_system.DTOs.RegisterUserDto;
import com.jerzymaj.hotel_guest_service_system.DTOs.UserDto;
import com.jerzymaj.hotel_guest_service_system.configuration.JwtProvider;
import com.jerzymaj.hotel_guest_service_system.mappers.UserMapper;
import com.jerzymaj.hotel_guest_service_system.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("hgss/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtTokenProvider;

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@Valid @RequestBody RegisterUserDto registerUserDto) {
        UserDto createdUserDto = userMapper.toDto(userService.registerUser(registerUserDto));

        return ResponseEntity.status(201).body(createdUserDto);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(),
                        loginRequest.password()
                )
        );

        return ResponseEntity.ok(jwtTokenProvider.generateToken(authentication));
    }
}
