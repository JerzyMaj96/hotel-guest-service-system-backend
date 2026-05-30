package com.jerzymaj.hotel_guest_service_system.services;

import com.jerzymaj.hotel_guest_service_system.DTOs.Notification;

public interface NotificationSender {
    void send(Notification notification);
}
