package com.it52.eventregistrationservice.impl;

import com.it52.eventregistrationservice.client.EventServiceClient;

import com.it52.eventregistrationservice.dto.EventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class EventServiceClientImpl implements EventServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(EventServiceClientImpl.class);

    private final WebClient webClient;

    public EventServiceClientImpl(@Qualifier("eventServiceWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public EventDto getEventById(String token, Long eventId) {
        logger.info("Sending request to event-service for eventId: {}", eventId);

        // Получение события
        return webClient.get()
                .uri("/api/events/{eventId}", eventId)
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .bodyToMono(EventDto.class)  // Предполагаем, что возвращаемый объект — EventDto
                .doOnNext(event -> logger.info("event-service responded: {}", event))
                .onErrorResume(e -> {
                    logger.error("Failed to get event", e);
                    return Mono.empty();  // Возвращаем пустое значение в случае ошибки
                })
                .blockOptional()  // Возвращает Optional<EventDto>
                .orElseThrow(() -> new IllegalArgumentException("Event not found for eventId: " + eventId));
    }
}
