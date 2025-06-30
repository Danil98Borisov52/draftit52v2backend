package com.it52.eventservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.it52.eventservice.config.ImageProxyConfig;
import com.it52.eventservice.config.MinioConfig;
import com.it52.eventservice.dto.registration.ParticipantDto;
import com.it52.eventservice.dto.user.UserChangeRequestDTO;
import com.it52.eventservice.model.EventParticipant;
import com.it52.eventservice.repository.ParticipantRepository;
import com.it52.eventservice.service.api.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParticipantServiceImpl implements ParticipantService {

    private static final Logger logger = LoggerFactory.getLogger(ParticipantServiceImpl.class);
    private final ObjectMapper objectMapper;
    private final ParticipantRepository participantRepository;
    private final MinioConfig minioConfig;
    private final ImageProxyConfig imageProxyConfig;

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
            UserChangeRequestDTO user = objectMapper.readValue(message, UserChangeRequestDTO.class);
            logger.info("Полученный пользователь: {}", user);

            List<EventParticipant> participants = participantRepository.findAllBySub(user.getSub());

            for (EventParticipant participant : participants) {
                participant.setAvatarImage(user.getAvatarImage());
                participant.setAnonymous(user.isAnonymous());
            }

            participantRepository.saveAll(participants);

        } catch (Exception e) {
            logger.error("Не удалось обработать изменение пользователя", e);
        }
    }


    @Override
    public List<EventParticipant> getParticipant(Long eventId) {
        List<EventParticipant> participants = participantRepository.findAllByEventId(eventId);

        for (EventParticipant participant : participants) {
            String originalImageUrl = participant.getAvatarImage();
            String titleImage = getAvatarImage(originalImageUrl, 300, 200, minioConfig, imageProxyConfig); // размеры задаются по нужде
            participant.setAvatarImage(titleImage);
        }
        return participants;
    }

    private static String getAvatarImage(String originalImageUrl, Integer width, Integer height, MinioConfig minioConfig, ImageProxyConfig imageProxyConfig) {
        if (originalImageUrl == null || originalImageUrl.isBlank()) {
            return null;
        }
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(imageProxyConfig.getBaseUrl())
                    .append("/")
                    .append(minioConfig.getUrl())
                    .append("/")
                    .append(minioConfig.getBucketUser())
                    .append("/")
                    .append(originalImageUrl);

            boolean hasParams = originalImageUrl.contains("?");
            if (width != null) {
                sb.append(hasParams ? "&" : "?").append("w=").append(width);
                hasParams = true;
            }
            if (height != null) {
                sb.append(hasParams ? "&" : "?").append("h=").append(height);
            }
            return sb.toString();
        } catch (Exception e) {
            System.err.println("Ошибка формирования URL для imageproxy: " + e.getMessage());
            return originalImageUrl;
        }
    }
}
