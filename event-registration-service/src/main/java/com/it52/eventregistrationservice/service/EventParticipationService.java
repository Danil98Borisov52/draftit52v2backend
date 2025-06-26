package com.it52.eventregistrationservice.service;

import com.it52.eventregistrationservice.client.EventServiceClient;
import com.it52.eventregistrationservice.client.UserServiceClient;
import com.it52.eventregistrationservice.dto.EventParticipationResponse;
import com.it52.eventregistrationservice.dto.UserEventsResponse;
import com.it52.eventregistrationservice.dto.UserRegisteredToEventDto;
import com.it52.eventregistrationservice.kafka.KafkaProducer;
import com.it52.eventregistrationservice.model.EventParticipation;
import com.it52.eventregistrationservice.repository.EventParticipationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class EventParticipationService {

    private final EventParticipationRepository repository;
    private final UserServiceClient userServiceClient;
    private final EventServiceClient eventServiceClient;
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
                    .avatarImage("http://minio:9000/event-images/1229_Бенедикт.jpg")
                    .anonymous(user.isAnonymous())
                    .startedAt(event.getStartedAt())
                    .title(event.getTitle())
                    .organizer(organizer)
                    .build();

            UserRegisteredToEventDto dto = new UserRegisteredToEventDto();
            dto.setSub(sub);
            dto.setEventId(eventId);
            dto.setSlug(event.getSlug());
            dto.setEmail(user.getEmail());
            dto.setUsername(user.getUsername());
            dto.setOrganizer(organizer);
            dto.setAnonymous(user.isAnonymous());
            dto.setFirstName(user.getFirstName());
            dto.setAvatarImage("http://minio:9000/event-images/1229_Бенедикт.jpg");
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

    public List<UserEventsResponse> getUserEvents(String sub) {
        var participations = repository.findBysub(sub);

        return participations.stream()
                .map(participation -> UserEventsResponse.builder()
                        .sub(participation.getSub())
                        .slug(participation.getSlug())
                        .startedAt(participation.getStartedAt())
                        .title(participation.getTitle())
                        .organizer(participation.isOrganizer())
                        .build())
                .collect(Collectors.toList());
    }
}
