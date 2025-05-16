package com.it52.user.kafka;

import com.it52.user.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, UserDTO> kafkaTemplate;

    public void sendNewUserEvent(UserDTO user) {
        kafkaTemplate.send("user.registered", user);
    }
}