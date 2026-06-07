package com.jerzymaj.hotel_guest_service_system.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationSenderFactory {

    private List<NotificationSender> senderList;

    public NotificationSender getFor(String recipient) {
        return senderList.stream()
                .filter(s -> s.supports(recipient))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No notification sender found for recipient: " + recipient));
    }
}
