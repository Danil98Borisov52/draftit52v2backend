package com.it52.authservice.service;

import com.it52.authservice.dto.OtpMessageEvent;
import com.it52.authservice.kafka.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final KafkaProducer kafkaProducer;

    public void sendOtp(String email, String otp) {
        OtpMessageEvent event = new OtpMessageEvent(email, otp);
        kafkaProducer.sendOtpEvent(event);
    }
}
