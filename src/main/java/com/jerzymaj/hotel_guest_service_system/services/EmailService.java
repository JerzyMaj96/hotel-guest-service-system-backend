package com.jerzymaj.hotel_guest_service_system.services;

import com.jerzymaj.hotel_guest_service_system.models.User;
import com.jerzymaj.hotel_guest_service_system.security.IAuthenticationFacade;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final IAuthenticationFacade authenticationFacade;


    public void sendNotificationEmail(User user, String title, String description) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(authenticationFacade.getAuthenticatedUserEmail());
        helper.setTo("tech@hotel.com");
        helper.setSubject(title + "-" + user.getFirstName() + " " + user.getLastName());
        helper.setText(description);

        javaMailSender.send(message);
    }

}
