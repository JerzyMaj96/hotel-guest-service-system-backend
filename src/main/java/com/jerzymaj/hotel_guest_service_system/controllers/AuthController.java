package com.jerzymaj.hotel_guest_service_system.controllers;

import com.jerzymaj.hotel_guest_service_system.DTOs.RegisterUserDto;
import com.jerzymaj.hotel_guest_service_system.DTOs.UserDto;
import com.jerzymaj.hotel_guest_service_system.models.User;
import com.jerzymaj.hotel_guest_service_system.services.UserService;
import com.jerzymaj.hotel_guest_service_system.translator.Translator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@Valid @RequestBody RegisterUserDto registerUserDto) {
        UserDto createdUserDto = Translator.convertUserToDto(userService.registerUser(registerUserDto));

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdUserDto.id())
                .toUri();

        return ResponseEntity.created(location).body(createdUserDto);
    }


}
