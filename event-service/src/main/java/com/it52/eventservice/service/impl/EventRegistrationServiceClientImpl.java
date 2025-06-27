package com.it52.eventservice.service.impl;

import com.it52.eventservice.dto.registration.EventParticipationRequestDTO;
import com.it52.eventservice.dto.registration.EventParticipationResponseDTO;
import com.it52.eventservice.service.api.EventRegistrationServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class EventRegistrationServiceClientImpl implements EventRegistrationServiceClient {
    private static final Logger logger = LoggerFactory.getLogger(EventRegistrationServiceClientImpl.class);

    private final WebClient webClient;

    public EventRegistrationServiceClientImpl(@Qualifier("eventRegistrationServiceWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public EventParticipationResponseDTO registrationOrganizer(String token, Long eventId) {
        logger.info("Sending participation request for eventId: {}", eventId);

        EventParticipationRequestDTO request = new EventParticipationRequestDTO(eventId, true);

        return webClient.post()
                .uri("/api/participations")
                .headers(headers -> headers.setBearerAuth(token))
                .bodyValue(request)
                .retrieve()
                .bodyToMono(EventParticipationResponseDTO.class)
                .doOnNext(response -> logger.info("event-registration-service responded: {}", response))
                .onErrorResume(e -> {
                    logger.error("Failed to register participation", e);
                    return Mono.empty();
                })
                .blockOptional()
                .orElseThrow(() -> new IllegalStateException("Failed to register participation for eventId: " + eventId));
    }
}
