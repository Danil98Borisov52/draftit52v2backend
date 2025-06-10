package com.it52.eventregistrationservice.service;

import com.it52.eventregistrationservice.client.EventServiceClient;
import com.it52.eventregistrationservice.client.UserServiceClient;
import com.it52.eventregistrationservice.model.EventParticipation;
import com.it52.eventregistrationservice.repository.EventParticipationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class EventParticipationService {

    private final EventParticipationRepository repository;

    @Autowired
    private final UserServiceClient userServiceClient;

    @Autowired
    private final EventServiceClient eventServiceClient;

    public EventParticipationService(EventParticipationRepository repository,
                                     UserServiceClient userServiceClient,
                                     EventServiceClient eventServiceClient) {
        this.repository = repository;
        this.userServiceClient = userServiceClient;
        this.eventServiceClient = eventServiceClient;
    }

    @Transactional
    public EventParticipation register(Long eventId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken) {
            String token = ((JwtAuthenticationToken) authentication).getToken().getTokenValue();
            String sub = ((JwtAuthenticationToken) authentication).getToken().getSubject();

            if (!userServiceClient.exists(token, sub)) {
                throw new IllegalArgumentException("User does not exist");
            }

            if (!eventServiceClient.exists(token, eventId)) {
                throw new IllegalArgumentException("Event does not exist");
            }

            if (repository.findBySubAndEventId(sub, eventId).isPresent()) {
                throw new IllegalStateException("User already registered for this event");
            }

            EventParticipation participation = new EventParticipation(sub, eventId);
            return repository.save(participation);
        } else {
            throw new IllegalStateException("No JWT authentication found in context");
        }
    }
}
