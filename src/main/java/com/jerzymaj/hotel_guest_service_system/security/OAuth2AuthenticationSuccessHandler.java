package com.jerzymaj.hotel_guest_service_system.security;

import com.jerzymaj.hotel_guest_service_system.configuration.JwtProvider;
import com.jerzymaj.hotel_guest_service_system.models.User;
import com.jerzymaj.hotel_guest_service_system.services.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

/**
 * Fires once Spring Security has successfully exchanged the authorization code
 * with the OAuth2 provider (Google) and loaded the user's profile.
 * <p>
 * It maps the external identity onto our own {@link User} record (creating one
 * on first login), then issues the same JWT the classic /login endpoint returns,
 * so the rest of the app (JwtAuthenticationFilter, role checks, etc.) doesn't
 * need to know how the user authenticated.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final UserService userService;

    @Value("${app.oauth2.redirect-uri}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        // authentication.getPrincipal() here is an OAuth2User (Google's profile),
        // NOT our own UserDetails - that's why we can't pass "authentication" straight
        // into jwtProvider.generateToken(), it expects a UserDetails principal.
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String firstName = oAuth2User.getAttribute("given_name");
        String lastName = oAuth2User.getAttribute("family_name");

        if (email == null || email.isBlank()) {
            log.warn("OAuth2 login succeeded but the provider did not return an email attribute");
            redirectWithError(response, "no_email_from_provider");
            return;
        }

        User user = userService.findOrCreateOAuth2User(email, firstName, lastName);

        // Rebuild an Authentication whose principal IS a UserDetails, so it can be
        // fed into the same JwtProvider used by the classic /login endpoint.
        Authentication userAuthentication = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());
        String jwt = jwtProvider.generateToken(userAuthentication);

        String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("token", jwt)
                .build()
                .toUriString();

        response.sendRedirect(targetUrl);
    }

    private void redirectWithError(HttpServletResponse response, String errorCode) throws IOException {
        String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("error", errorCode)
                .build()
                .toUriString();

        response.sendRedirect(targetUrl);
    }
}
