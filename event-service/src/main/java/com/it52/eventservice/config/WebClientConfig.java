package com.it52.eventservice.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${eventregistration.service.url}")
    private String eventRegistrationServiceUrl;

    @Bean
    @Qualifier("eventRegistrationServiceWebClient")
    public WebClient userServiceWebClient() {
        return WebClient.builder()
                .baseUrl(eventRegistrationServiceUrl)
                .build();
    }
}
