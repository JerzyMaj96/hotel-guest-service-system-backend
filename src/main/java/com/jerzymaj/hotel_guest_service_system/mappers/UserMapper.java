package com.jerzymaj.hotel_guest_service_system.mappers;

import com.jerzymaj.hotel_guest_service_system.DTOs.UserDto;
import com.jerzymaj.hotel_guest_service_system.models.User;

public final class UserMapper {

    public static UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(),
                user.getPhoneNumber(), user.getCreationDate());
    }
}
