package com.jerzymaj.hotel_guest_service_system.configuration;

import com.jerzymaj.hotel_guest_service_system.services.NotificationSender;
import com.jerzymaj.hotel_guest_service_system.services.RetryingNotificationSender;
import com.jerzymaj.hotel_guest_service_system.services.impl.SmsNotificationSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationConfig {

    @Bean
    public NotificationSender smsNotificationSender(@Value("${app.twilio.phone-number}")  String fromPhoneNumber) {
        NotificationSender sender = new SmsNotificationSender(fromPhoneNumber);
        return new RetryingNotificationSender(sender, 4);
    }
}
