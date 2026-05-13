package com.jerzymaj.hotel_guest_service_system.services;

import com.jerzymaj.hotel_guest_service_system.models.User;

public interface EmailService {
    void sendNotificationEmail(User user, String title, String description);
}
