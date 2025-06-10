package com.it52.eventregistrationservice.impl;

import com.it52.eventregistrationservice.client.EventServiceClient;

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
    public boolean exists(String token, Long eventId) {
        logger.info("Sending request to event-service for eventId: {}", eventId);

        return Boolean.TRUE.equals(webClient.get()
                .uri("/api/events/{eventId}/exists", eventId)
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .bodyToMono(Boolean.class)
                .doOnNext(result -> logger.info("event-service responded: {}", result))
                .onErrorResume(e -> {
                    logger.error("Failed to get event exists status", e);
                    return Mono.just(false);
                })
                .block());
    }
}
