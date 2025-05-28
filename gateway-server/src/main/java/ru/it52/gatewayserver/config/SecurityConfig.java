package ru.it52.gatewayserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final ReactiveClientRegistrationRepository registrationRepository;

    public SecurityConfig(ReactiveClientRegistrationRepository registrationRepository) {
        this.registrationRepository = registrationRepository;
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf().disable()
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/realms/**").permitAll() // Доступ к Keycloak без аутентификации
                        .anyExchange().authenticated()         // Все остальные запросы требуют аутентификации
                )
                .oauth2Login()// Перенаправление на Keycloak для логина
                .and()
                .logout()
                .logoutSuccessHandler(logoutSuccessHandler())
                .and()
                .oauth2ResourceServer()
                .jwt(jwt -> jwt
                        .jwtDecoder(jwtDecoder())
                );
        return http.build();
    }

    @Bean
    public ServerLogoutSuccessHandler logoutSuccessHandler() {
        RedirectServerLogoutSuccessHandler successHandler = new RedirectServerLogoutSuccessHandler();
        successHandler.setLogoutSuccessUrl(URI.create(
                "http://localhost:8085/oauth2/authorization/keycloak"));
        return successHandler;
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        String jwkSetUri = "http://keycloak:8080/realms/it52/protocol/openid-connect/certs";
        WebClient webClient = WebClient.builder().build();
        NimbusReactiveJwtDecoder jwtDecoder = NimbusReactiveJwtDecoder.withJwkSetUri(jwkSetUri)
                .webClient(webClient)
                .build();
        // Логирование ошибок декодирования
        return token -> jwtDecoder.decode(token)
                .onErrorResume(e -> {
                    System.err.println("Ошибка декодирования JWT: " + e.getMessage());
                    return Mono.error(e);
                });
    }
    @Bean
    public JwtAuthenticationConverter jwtAuthConverter() {
        return new JwtAuthenticationConverter();
    }
}
