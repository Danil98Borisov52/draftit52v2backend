package com.it52.eventregistrationservice.kafka;

import com.it52.eventregistrationservice.dto.UserRegisteredToEventDTO;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducer {

    private final KafkaTemplate<String, UserRegisteredToEventDTO> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, UserRegisteredToEventDTO> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUserRegisteredToEvent(UserRegisteredToEventDTO userRegisteredToEventDto) {
        kafkaTemplate.send("user_registered_to_event", userRegisteredToEventDto);
    }
}
