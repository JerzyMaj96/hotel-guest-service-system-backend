package com.jerzymaj.hotel_guest_service_system.DTOs;

public record Notification(String recipient, String title, String description, String senderEmail) {

    public Notification(String recipient, String title, String description) {
        this(recipient, title, description, null);
    }
}
