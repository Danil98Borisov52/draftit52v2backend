package com.it52.user.kafka;

import com.it52.user.model.User;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducer {

    private final KafkaTemplate<String, User> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, User> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendNewUserEvent(User user) {
        kafkaTemplate.send("user_registered", user);
    }

    public void sendUserChanges(User user) {
        kafkaTemplate.send("user_changed", user);
    }
}