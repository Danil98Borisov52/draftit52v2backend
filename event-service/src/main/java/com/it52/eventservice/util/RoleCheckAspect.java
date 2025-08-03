package com.it52.eventservice.util;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@Aspect
public class RoleCheckAspect {

    private final HttpServletRequest request;

    public RoleCheckAspect(HttpServletRequest request) {
        this.request = request;
    }

    @Around("@annotation(requireRole)")
    public Object checkRole(ProceedingJoinPoint joinPoint, RequireRole requireRole) throws Throwable {
        String roleHeader = request.getHeader("X-User-Role");
        if (roleHeader == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing role header");
        }

        int userRole;
        try {
            userRole = Integer.parseInt(roleHeader);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role header");
        }

        if (userRole != requireRole.value()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied for role: " + userRole);
        }

        return joinPoint.proceed();
    }
}
