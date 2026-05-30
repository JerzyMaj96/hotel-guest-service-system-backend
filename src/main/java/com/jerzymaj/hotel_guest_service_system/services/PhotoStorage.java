package com.jerzymaj.hotel_guest_service_system.services;

import org.springframework.web.multipart.MultipartFile;

public interface PhotoStorage {
    String savePhoto(MultipartFile photo);
}
