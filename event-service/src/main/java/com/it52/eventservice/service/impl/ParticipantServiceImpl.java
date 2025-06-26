package com.it52.eventservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.it52.eventservice.dto.ParticipantDto;
import com.it52.eventservice.dto.UserChangedEvent;
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
            participant.setSlug(participantDto.getSlug());
            participant.setRegisteredAt(participantDto.getRegisteredAt());
            participant.setAvatarImage("http://minio:9000/event-images/1229_Бенедикт.jpg");
            participant.setAnonymous(participantDto.isAnonymous());
            participant.setOrganizer(participantDto.isOrganizer());

            participantRepository.save(participant);
            logger.info("Участник сохранён: {}", participant);
        } catch (Exception e) {
            logger.error("Ошибка обработки Kafka-сообщения: {}", message, e);
        }
    }

    @Override
    @KafkaListener(topics = "user_changed", groupId = "event-group")
    public void listenUserChanged(String message) {
        try {
            UserChangedEvent user = objectMapper.readValue(message, UserChangedEvent.class);
            logger.info("Received user_changed event: {}", user);

            List<EventParticipant> participants = participantRepository.findAllBySub(user.getSub());

            for (EventParticipant participant : participants) {
                participant.setAvatarImage(user.getAvatarImage());
                participant.setAnonymous(user.isAnonymous());
            }

            participantRepository.saveAll(participants);

        } catch (Exception e) {
            logger.error("Failed to process user_changed event", e);
        }
    }


    @Override
    public List<EventParticipant> getParticipant(Long eventId) {
        return participantRepository.findAllByEventId(eventId);
    }
}
