package com.it52.authservice.dto;

import lombok.Data;

@Data
public class OtpVerifyRequest {
    private String username;
    private String code;
}
