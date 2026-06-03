package com.jerzymaj.hotel_guest_service_system.exceptions;

import java.time.LocalDateTime;

public record ErrorDetails(LocalDateTime timeStamp, String message,
                           String path) {
}
