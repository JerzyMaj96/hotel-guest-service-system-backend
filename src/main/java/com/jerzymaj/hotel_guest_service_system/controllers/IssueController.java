package com.jerzymaj.hotel_guest_service_system.controllers;

import com.jerzymaj.hotel_guest_service_system.DTOs.IssueCreateRequestDto;
import com.jerzymaj.hotel_guest_service_system.DTOs.IssueResponseDto;
import com.jerzymaj.hotel_guest_service_system.enums.IssueStatus;
import com.jerzymaj.hotel_guest_service_system.services.IssueService;
import com.jerzymaj.hotel_guest_service_system.translator.Translator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("hgss/api/issues")
@RequiredArgsConstructor
public class IssueController {

    private final IssueService issueService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<IssueResponseDto> createIssue(@RequestPart(value = "photo", required = false) MultipartFile photo,
                                                        @Valid @RequestPart(value = "issue") IssueCreateRequestDto issueCreateRequestDto) {

        IssueResponseDto issueResponseDto = Translator.convertIssueToDto(issueService.createIssue(photo, issueCreateRequestDto));

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(issueResponseDto.id())
                .toUri();

        return ResponseEntity.created(location).body(issueResponseDto);
    }

    @GetMapping("/user-prof")
    public ResponseEntity<List<IssueResponseDto>> getAllIssuesForUser() {

        List<IssueResponseDto> issueResponseDtoList = issueService.findAllIssuesForAuthenticatedUser().stream()
                .map(Translator::convertIssueToDto)
                .toList();

        return ResponseEntity.ok(issueResponseDtoList);
    }

    @GetMapping("/tech-prof")
    public ResponseEntity<List<IssueResponseDto>> getAllIssuesForTech() {
        List<IssueResponseDto> issueResponseDtoList = issueService.findAllIssues().stream()
                .map(Translator::convertIssueToDto)
                .toList();

        return ResponseEntity.ok(issueResponseDtoList);
    }

    @GetMapping("/photos/{fileName}")
    public ResponseEntity<Resource> getPhoto (@PathVariable String fileName) throws MalformedURLException {

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(issueService.getPhoto(fileName));
    }

    @PatchMapping("/{issueId}/status")
    public ResponseEntity<Void> updateIssueStatus(@PathVariable Long issueId, @RequestParam IssueStatus issueStatus) {
        issueService.updateIssueStatus(issueId, issueStatus);
        return ResponseEntity.noContent().build();
    }
}
