package com.jerzymaj.hotel_guest_service_system.services.impl;

import com.jerzymaj.hotel_guest_service_system.exceptions.PhotoStorageException;
import com.jerzymaj.hotel_guest_service_system.services.PhotoStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class PhotoStorageImpl implements PhotoStorage {

    @Value("${storage.upload-dir:upload-dir}")
    private String uploadDir;

    @Override
    public String savePhoto(MultipartFile photo) {
        try {
            String fileName = UUID.randomUUID() + "_" + photo.getOriginalFilename();
            Path uploadDirectory = Paths.get(uploadDir).toAbsolutePath();
            if (!Files.exists(uploadDirectory)) {
                Files.createDirectories(uploadDirectory);
            }
            Path filePath = uploadDirectory.resolve(fileName);
            Files.copy(photo.getInputStream(), filePath);
            return fileName;
        } catch (IOException ex) {
            throw new PhotoStorageException("Failed to save photo",ex);
        }
    }
}
