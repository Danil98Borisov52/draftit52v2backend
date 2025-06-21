package com.it52.eventservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.it52.eventservice.dto.ParticipantDto;
import com.it52.eventservice.model.Author;
import com.it52.eventservice.model.EventParticipant;
import com.it52.eventservice.repository.ParticipantRepository;
import com.it52.eventservice.service.api.ParticipantService;
import com.it52.eventservice.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParticipantServiceImpl implements ParticipantService {

    private static final Logger logger = LoggerFactory.getLogger(ParticipantServiceImpl.class);
    private final ObjectMapper objectMapper;
    private final ParticipantRepository participantRepository;


    @Override
    @KafkaListener(topics = "user_registered_to_event", groupId = "event-group")
    public void listenUserRegisteredToEvent(String message) {
        try {
            ParticipantDto participantDto = objectMapper.readValue(message, ParticipantDto.class);
            logger.info("Получено событие: {}", message);

            EventParticipant participant = new EventParticipant();
            participant.setEventId(participantDto.getEventId());
            participant.setSub(participantDto.getSub());
            participant.setRegisteredAt(participantDto.getRegisteredAt());
            participant.setAvatarImage(participantDto.getAvatarImage());
            participant.setOrganizer(participantDto.isOrganizer());

            participantRepository.save(participant);
            logger.info("Участник сохранён: {}", participant);
        } catch (Exception e) {
            logger.error("Ошибка обработки Kafka-сообщения: {}", message, e);
        }
    }

    @Override
    public List<EventParticipant> getParticipant(Long eventId) {
        return participantRepository.findAllByEventId(eventId);
    }
}
