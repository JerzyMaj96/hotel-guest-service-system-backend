package com.jerzymaj.hotel_guest_service_system.controllers;

import com.jerzymaj.hotel_guest_service_system.DTOs.IssueCreateRequestDto;
import com.jerzymaj.hotel_guest_service_system.DTOs.IssueResponseDto;
import com.jerzymaj.hotel_guest_service_system.enums.IssueStatus;
import com.jerzymaj.hotel_guest_service_system.services.IssueService;
import com.jerzymaj.hotel_guest_service_system.translator.Translator;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("hgss/api/issues")
@RequiredArgsConstructor
public class IssueController {

    private final IssueService issueService;

    @PostMapping
    public ResponseEntity<IssueResponseDto> createIssue(@Valid @RequestBody IssueCreateRequestDto issueCreateRequestDto) throws MessagingException {

        IssueResponseDto issueResponseDto = Translator.convertIssueToDto(issueService.createIssue(issueCreateRequestDto));

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(issueResponseDto.id())
                .toUri();

        return ResponseEntity.created(location).body(issueResponseDto);
    }

    @GetMapping
    public ResponseEntity<List<IssueResponseDto>> getAllIssues(@RequestParam Long userId) {

        List<IssueResponseDto> issueResponseDtoList = issueService.findAllIssuesByUserId(userId).stream()
                .map(Translator::convertIssueToDto)
                .toList();

        return ResponseEntity.ok(issueResponseDtoList);
    }

    @PatchMapping("/{issueId}/status")
    public ResponseEntity<Void> updateIssueStatus(@PathVariable Long issueId, @RequestParam IssueStatus issueStatus) {
        issueService.updateIssueStatus(issueId, issueStatus);
        return ResponseEntity.noContent().build();
    }
}
