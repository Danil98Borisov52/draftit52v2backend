package com.it52.eventservice.util;

import org.springframework.security.core.Authentication;
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

    /*    public static String getCurrentUsername() {
            JwtAuthenticationToken authentication =
                    (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

            Jwt jwt = authentication.getToken();
            return jwt.getClaimAsString("preferred_username");
        }*/
    public static String getCurrentUsername() {
        JwtAuthenticationToken authentication =
                (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        Jwt jwt = authentication.getToken();
        String givenName = jwt.getClaimAsString("given_name");
        String familyName = jwt.getClaimAsString("family_name");

        return familyName + " " + givenName; // или givenName + " " + familyName, по желанию
    }

    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("No authentication found in security context.");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt jwt) {
            return jwt.getClaimAsString("email");
        }

        throw new IllegalStateException("Principal is not of type Jwt");
    }
}
