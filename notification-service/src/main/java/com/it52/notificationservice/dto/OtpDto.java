package com.it52.notificationservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OtpDto {
    private String email;
    private String otp;
    private LocalDateTime expiresAt;
}
