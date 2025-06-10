package com.it52.eventregistrationservice.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${user.service.url}")
    private String userServiceUrl;

    @Value("${event.service.url}")
    private String eventServiceUrl;

    @Bean
    @Qualifier("userServiceWebClient")
    public WebClient userServiceWebClient() {
        return WebClient.builder()
                .baseUrl(userServiceUrl)
                .build();
    }

    @Bean
    @Qualifier("eventServiceWebClient")
    public WebClient eventServiceWebClient() {
        return WebClient.builder()
                .baseUrl(eventServiceUrl)
                .build();
    }
}
