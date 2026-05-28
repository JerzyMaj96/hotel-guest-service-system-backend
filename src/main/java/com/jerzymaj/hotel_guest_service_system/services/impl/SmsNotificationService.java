package com.jerzymaj.hotel_guest_service_system.services.impl;

import com.jerzymaj.hotel_guest_service_system.services.NotificationService;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class SmsNotificationService implements NotificationService {

    @Value("${twilio.phone-number}")
    private String fromPhoneNumber;

    @Override
    @Async
    public void send(String recipient, String title, String description) {
        try {
            String fullMessage = title + "\n" + description;

            Message message = Message.creator(
                    new PhoneNumber(recipient),
                    new PhoneNumber(fromPhoneNumber),
                    fullMessage
            ).create();

            log.info("SMS sent successfully. SID: {}", message.getSid());

        } catch (Exception ex) {
            log.error("Failed to send SMS to {}: {}", recipient, ex.getMessage());
        }
    }
}
