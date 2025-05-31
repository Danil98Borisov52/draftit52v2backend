package com.it52.user.kafka;

import com.it52.user.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducer {

    private final KafkaTemplate<String, UserDTO> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, UserDTO> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendNewUserEvent(UserDTO user) {
        kafkaTemplate.send("user_registered", user);
    }
}