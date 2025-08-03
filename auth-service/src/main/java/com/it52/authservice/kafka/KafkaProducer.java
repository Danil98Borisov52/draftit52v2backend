package com.it52.authservice.kafka;

import com.it52.authservice.model.UserRegistration;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducer {

    private final KafkaTemplate<String, UserRegistration> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, UserRegistration> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendNewUserEvent(UserRegistration user) {
        kafkaTemplate.send("user_registered", user);
    }

    public void sendUserChanges(UserRegistration user) {
        kafkaTemplate.send("user_changed", user);
    }
}