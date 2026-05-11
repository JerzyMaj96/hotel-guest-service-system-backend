package com.jerzymaj.hotel_guest_service_system.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class StorageService {

    @Value("${storage.upload-dir:upload-dir}")
    private String uploadDir;

    public String savePhoto(MultipartFile photo) throws IOException {
        String fileName = UUID.randomUUID() + "_" + photo.getOriginalFilename();
        Path uploadDirectory = Paths.get(uploadDir).toAbsolutePath();
        if (!Files.exists(uploadDirectory)) {
            Files.createDirectories(uploadDirectory);
        }
        Path filePath = uploadDirectory.resolve(fileName);
        Files.copy(photo.getInputStream(), filePath);
        return fileName;
    }
}
