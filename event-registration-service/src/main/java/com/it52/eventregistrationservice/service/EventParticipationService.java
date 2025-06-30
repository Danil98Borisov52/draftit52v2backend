package com.it52.eventregistrationservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.it52.eventregistrationservice.client.EventServiceClient;
import com.it52.eventregistrationservice.client.UserServiceClient;
import com.it52.eventregistrationservice.dto.UserChangeRequestDTO;
import com.it52.eventregistrationservice.dto.ParticipationResponseDTO;
import com.it52.eventregistrationservice.dto.UserRegisteredToEventDTO;
import com.it52.eventregistrationservice.kafka.KafkaProducer;
import com.it52.eventregistrationservice.model.EventParticipation;
import com.it52.eventregistrationservice.repository.EventParticipationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class EventParticipationService {

    private static final Logger logger = LoggerFactory.getLogger(EventParticipationService.class);
    private final EventParticipationRepository repository;
    private final UserServiceClient userServiceClient;
    private final EventServiceClient eventServiceClient;
    private final ObjectMapper objectMapper;
    private final KafkaProducer kafkaProducer;

    @Transactional
    public EventParticipation register(Long eventId, boolean organizer) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken) {
            String token = ((JwtAuthenticationToken) authentication).getToken().getTokenValue();
            String sub = ((JwtAuthenticationToken) authentication).getToken().getSubject();

            var user = userServiceClient.getBySub(token, sub);
            if (user == null) {
                throw new IllegalArgumentException("User does not exist");
            }

            var event = eventServiceClient.getEventById(token, eventId);
            if (event == null) {
                throw new IllegalArgumentException("Event does not exist");
            }

            if (repository.findBySubAndEventId(sub, eventId).isPresent()) {
                throw new IllegalStateException("User already registered for this event");
            }

            EventParticipation participation = EventParticipation.builder()
                    .sub(sub)
                    .eventId(eventId)
                    .slug(event.getSlug())
                    .avatarImage(user.getAvatarImage())
                    .anonymous(user.isAnonymous())
                    .startedAt(event.getStartedAt())
                    .title(event.getTitle())
                    .organizer(organizer)
                    .build();

            UserRegisteredToEventDTO dto = new UserRegisteredToEventDTO();
            dto.setSub(sub);
            dto.setEventId(eventId);
            dto.setSlug(event.getSlug());
            dto.setEmail(user.getEmail());
            dto.setUsername(user.getUsername());
            dto.setOrganizer(organizer);
            dto.setAnonymous(user.isAnonymous());
            dto.setFirstName(user.getFirstName());
            dto.setAvatarImage(user.getAvatarImage());
            dto.setEventTitle(event.getTitle());
            dto.setEventDate(event.getStartedAt());
            dto.setEventPlace(event.getPlace());
            dto.setRegisteredAt(LocalDateTime.now());

            kafkaProducer.sendUserRegisteredToEvent(dto);
            return repository.save(participation);

        } else {
            throw new IllegalStateException("No JWT authentication found in context");
        }
    }

    @KafkaListener(topics = "user_changed", groupId = "event-registration-group")
    public void listenUserChanged(String message) {
        try {
            UserChangeRequestDTO user = objectMapper.readValue(message, UserChangeRequestDTO.class);
            logger.info("Received user_changed user: {}", user);

            List<EventParticipation> participants = repository.findBysub(user.getSub());

            for (EventParticipation participant : participants) {
                participant.setAvatarImage(user.getAvatarImage());
                participant.setAnonymous(user.isAnonymous());
            }

            repository.saveAll(participants);

        } catch (Exception e) {
            logger.error("Failed to process user_changed user", e);
        }
    }

    public List<ParticipationResponseDTO> getUserEvents(String sub) {
        var participations = repository.findBysub(sub);

        return participations.stream()
                .map(participation -> ParticipationResponseDTO.builder()
                        .sub(participation.getSub())
                        .slug(participation.getSlug())
                        .startedAt(participation.getStartedAt())
                        .title(participation.getTitle())
                        .organizer(participation.isOrganizer())
                        .build())
                .collect(Collectors.toList());
    }
}
