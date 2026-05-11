package com.jerzymaj.hotel_guest_service_system.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFacadeImpl implements AuthenticationFacade {
    @Override
    public String getAuthenticatedUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
