package com.jerzymaj.hotel_guest_service_system.services.impl;

import com.jerzymaj.hotel_guest_service_system.DTOs.Notification;
import com.jerzymaj.hotel_guest_service_system.services.NotificationSender;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class SmsNotificationSender implements NotificationSender {

    @Value("${app.twilio.phone-number}")
    private String fromPhoneNumber;

    @Override
    @Async
    public void send(Notification notification) {
        try {
            String fullMessage = notification.title() + "\n" + notification.description();

            Message message = Message.creator(
                    new PhoneNumber(notification.recipient()),
                    new PhoneNumber(fromPhoneNumber),
                    fullMessage
            ).create();

            log.info("SMS sent successfully. SID: {}", message.getSid());

        } catch (Exception ex) {
            log.error("Failed to send SMS to {}: {}", notification.recipient(), ex.getMessage());
        }
    }

    @Override
    public boolean supports(String recipient) {
        return recipient.matches("^\\+[1-9][0-9]{7,14}$");
    }
}
