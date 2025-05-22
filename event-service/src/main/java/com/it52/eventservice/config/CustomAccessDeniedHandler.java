package com.it52.eventservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.it52.eventservice.error.Message;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import com.it52.eventservice.error.Error;

import java.io.IOException;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.getOutputStream().println(objectMapper.writerWithDefaultPrettyPrinter().
                writeValueAsString(
                        new Message(false,
                                new Error("Forbidden", 403))));
    }
}
