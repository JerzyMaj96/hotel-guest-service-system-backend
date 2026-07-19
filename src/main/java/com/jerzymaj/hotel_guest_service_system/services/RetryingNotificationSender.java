package com.jerzymaj.hotel_guest_service_system.services;

import com.jerzymaj.hotel_guest_service_system.DTOs.Notification;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RetryingNotificationSender implements NotificationSender {

    private final NotificationSender delegate;
    private final int maxRetries;

    public RetryingNotificationSender(NotificationSender delegate, int maxRetries) {
        this.delegate = delegate;
        this.maxRetries = maxRetries;
    }


    @Override
    public void send(Notification notification) {
        int retry = 0;
        while (true) {
            try {
                delegate.send(notification);
                return;
            } catch (Exception ex) {
                if (retry++ >= maxRetries) {
                    log.error("Number of retries has reached it's limit {}", notification.recipient());
                    throw ex;
                }

                log.warn("Retry {} failed for {}, retrying...", retry, notification.recipient());
            }
        }
    }

    @Override
    public boolean supports(String recipient) {
        return delegate.supports(recipient);
    }
}
