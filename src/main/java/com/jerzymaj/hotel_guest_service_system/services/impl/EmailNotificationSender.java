package com.jerzymaj.hotel_guest_service_system.services.impl;

import com.jerzymaj.hotel_guest_service_system.DTOs.Notification;
import com.jerzymaj.hotel_guest_service_system.security.AuthenticationFacade;
import com.jerzymaj.hotel_guest_service_system.services.NotificationSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailNotificationSender implements NotificationSender {

    private final JavaMailSender javaMailSender;
    private final AuthenticationFacade authenticationFacade;

    @Override
    @Async
    public void send(Notification notification) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(authenticationFacade.getAuthenticatedUserEmail());
            helper.setTo(notification.recipient());
            helper.setSubject(notification.title());
            helper.setText(notification.description());

            javaMailSender.send(message);
        } catch (MessagingException ex) {
            log.error("Failed to send async email notification", ex);
        }
    }
}
