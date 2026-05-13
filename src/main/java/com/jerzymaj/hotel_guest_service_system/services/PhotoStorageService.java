package com.jerzymaj.hotel_guest_service_system.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PhotoStorageService {
    String savePhoto(MultipartFile photo);
}
