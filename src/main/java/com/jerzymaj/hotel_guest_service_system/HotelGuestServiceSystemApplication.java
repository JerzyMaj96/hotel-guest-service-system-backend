package com.jerzymaj.hotel_guest_service_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class HotelGuestServiceSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotelGuestServiceSystemApplication.class, args);
    }

}
