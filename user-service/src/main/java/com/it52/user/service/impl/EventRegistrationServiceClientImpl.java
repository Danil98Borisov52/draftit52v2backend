package com.it52.user.service.impl;

import com.it52.user.dto.UserEventParticipationDTO;
import com.it52.user.service.api.EventRegistrationServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Component
public class EventRegistrationServiceClientImpl implements EventRegistrationServiceClient {
    private static final Logger logger = LoggerFactory.getLogger(EventRegistrationServiceClientImpl.class);

    private final WebClient webClient;

    public EventRegistrationServiceClientImpl(@Qualifier("eventRegistrationServiceWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public List<UserEventParticipationDTO> getUserEvents(String sub) {
        logger.info("Fetching participation list for user with sub: {}", sub);

        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            String token = jwtAuth.getToken().getTokenValue();

            return webClient.get()
                    .uri("/api/participations/user/{sub}", sub)
                    .headers(headers -> headers.setBearerAuth(token))
                    .retrieve()
                    .bodyToFlux(UserEventParticipationDTO.class)
                    .collectList()
                    .doOnNext(list -> logger.info("event-registration-service returned {} participations", list.size()))
                    .onErrorResume(e -> {
                        logger.error("Failed to fetch user participations", e);
                        return Mono.empty();
                    })
                    .blockOptional()
                    .orElse(Collections.emptyList()); // если ничего не пришло — возвращаем пустой список
        } else {
            throw new IllegalStateException("No JWT authentication found in SecurityContext");
        }
    }
}
