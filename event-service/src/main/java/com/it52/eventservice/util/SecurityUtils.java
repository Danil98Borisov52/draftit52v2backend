package com.it52.eventservice.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class SecurityUtils {
    public static String getCurrentUserId() {
        JwtAuthenticationToken authentication =
                (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = authentication.getToken();
        return jwt.getClaimAsString("sub");
    }

    public static String getCurrentUsername() {
        JwtAuthenticationToken authentication =
                (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        Jwt jwt = authentication.getToken();
        return jwt.getClaimAsString("preferred_username");
    }
}
