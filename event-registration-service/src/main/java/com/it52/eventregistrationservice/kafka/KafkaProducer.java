package com.it52.eventregistrationservice.kafka;

import com.it52.eventregistrationservice.dto.UserRegisteredToEventDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducer {

    private final KafkaTemplate<String, UserRegisteredToEventDto> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, UserRegisteredToEventDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUserRegisteredToEvent(UserRegisteredToEventDto userRegisteredToEventDto) {
        kafkaTemplate.send("user_registered_to_event", userRegisteredToEventDto);
    }
}
