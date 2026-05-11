package com.jerzymaj.hotel_guest_service_system.services;

import com.jerzymaj.hotel_guest_service_system.models.User;
import com.jerzymaj.hotel_guest_service_system.security.AuthenticationFacade;
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
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final AuthenticationFacade authenticationFacade;

    @Async
    public void sendNotificationEmail(User user, String title, String description) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(authenticationFacade.getAuthenticatedUserEmail());
            helper.setTo("tech@hotel.com");
            helper.setSubject(title + "-" + user.getFirstName() + " " + user.getLastName());
            helper.setText(description);

            javaMailSender.send(message);
        } catch (MessagingException ex) {
            log.error("Failed to send async email notification", ex);
        }
    }

}
