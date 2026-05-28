package com.jerzymaj.hotel_guest_service_system.services;

import com.jerzymaj.hotel_guest_service_system.models.User;

public interface NotificationService {
    void sendEmail(User user, String title, String description);

    void sendSMS(User user, String title);
}
