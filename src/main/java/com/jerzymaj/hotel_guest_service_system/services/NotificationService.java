package com.jerzymaj.hotel_guest_service_system.services;

public interface NotificationService {
    void send(String recipient, String title, String description);
}
