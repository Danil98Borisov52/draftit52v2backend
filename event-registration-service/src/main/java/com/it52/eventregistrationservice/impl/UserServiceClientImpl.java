package com.it52.eventregistrationservice.impl;

import com.it52.eventregistrationservice.client.UserServiceClient;

import com.it52.eventregistrationservice.dto.UserDTO;
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
    public UserDTO getBySub(String token, String sub) {
        logger.info("Sending request to user-service for userId: {}", sub);

        // Получение пользователя, если он существует
        return webClient.get()
                .uri("/api/users/profile/{sub}", sub)
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .bodyToMono(UserDTO.class)  // Предполагается, что объект пользователя будет в ответе
                .doOnNext(user -> logger.info("user-service responded: {}", user))
                .onErrorResume(e -> {
                    logger.error("Failed to get user", e);
                    return Mono.empty(); // Возвращаем пустое значение в случае ошибки
                })
                .blockOptional()  // Возвращает Optional<User>, если пользователь не найден, это будет пустое значение
                .orElseThrow(() -> new IllegalArgumentException("User not found for sub: " + sub));
    }
}