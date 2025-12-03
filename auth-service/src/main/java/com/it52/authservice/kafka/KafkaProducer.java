package com.it52.authservice.kafka;

import com.it52.authservice.dto.OtpMessageEvent;
import com.it52.authservice.model.UserRegistration;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendNewUserEvent(UserRegistration user) {
        kafkaTemplate.send("user_registered", user);
    }

    public void sendOtpEvent(OtpMessageEvent event) {
        kafkaTemplate.send("otp-email-topic", event);
    }
}