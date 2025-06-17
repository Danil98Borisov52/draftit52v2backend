package com.it52.eventregistrationservice.service;

import com.it52.eventregistrationservice.client.EventServiceClient;
import com.it52.eventregistrationservice.client.UserServiceClient;
import com.it52.eventregistrationservice.dto.EventDto;
import com.it52.eventregistrationservice.dto.UserDTO;
import com.it52.eventregistrationservice.dto.UserRegisteredToEventDto;
import com.it52.eventregistrationservice.kafka.KafkaProducer;
import com.it52.eventregistrationservice.model.EventParticipation;
import com.it52.eventregistrationservice.repository.EventParticipationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EventParticipationService {

    private final EventParticipationRepository repository;

    @Autowired
    private final UserServiceClient userServiceClient;

    @Autowired
    private final EventServiceClient eventServiceClient;

    @Autowired
    private final KafkaProducer kafkaProducer;

    public EventParticipationService(EventParticipationRepository repository,
                                     UserServiceClient userServiceClient,
                                     EventServiceClient eventServiceClient,
                                     KafkaProducer kafkaProducer) {
        this.repository = repository;
        this.userServiceClient = userServiceClient;
        this.eventServiceClient = eventServiceClient;
        this.kafkaProducer = kafkaProducer;

    }

    @Transactional
    public EventParticipation register(Long eventId) {
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

            EventParticipation participation = new EventParticipation(sub, eventId, user.getAvatarImage());

            UserRegisteredToEventDto dto = new UserRegisteredToEventDto();
            dto.setSub(sub);
            dto.setEventId(eventId);
            dto.setEmail(user.getEmail());
            dto.setUsername(user.getUsername());
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
}
