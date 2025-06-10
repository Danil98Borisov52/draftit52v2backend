package com.it52.eventregistrationservice.impl;

import com.it52.eventregistrationservice.client.UserServiceClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class UserServiceClientImpl implements UserServiceClient {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceClientImpl.class);

    private final WebClient webClient;

    public UserServiceClientImpl(@Qualifier("userServiceWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public boolean exists(String token, String sub) {
        logger.info("Sending request to user-service for userId: {}", sub);
        return Boolean.TRUE.equals(webClient.get()
                .uri("/api/users/{sub}/exists", sub)
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .bodyToMono(Boolean.class)
                .doOnNext(result -> logger.info("user-service responded: {}", result))
                .onErrorResume(e -> {
                    logger.error("Failed to get event exists status", e);
                    return Mono.just(false);
                })
                .block());
    }
}