package ru.it52.gatewayserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;

import reactor.core.publisher.Mono;

import java.net.URI;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfg {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, ReactiveClientRegistrationRepository registrationRepository) {
        http
                .csrf().disable()
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/login/**", "/oauth2/**").permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .authenticationSuccessHandler(redirectToUsersHandler())
                )
                .logout()
                .logoutSuccessHandler(oidcLogoutSuccessHandler(registrationRepository))
        ;

        return http.build();
    }

    @Bean
    public ServerAuthenticationSuccessHandler redirectToUsersHandler() {
        return (webFilterExchange, authentication) -> {
            Object principal = authentication.getPrincipal();

            if (principal instanceof org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser oidcUser) {
                String sub = oidcUser.getSubject(); // subject claim (usually UUID)
                URI redirectUri = URI.create("/api/users/profile/" + sub);

                ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
                response.setStatusCode(HttpStatus.FOUND);
                response.getHeaders().setLocation(redirectUri);

                return response.setComplete();
            }

            // fallback: internal error
            return Mono.error(new IllegalStateException("Unexpected principal type: " + principal.getClass()));
        };
    }

    @Bean
    public ServerLogoutSuccessHandler oidcLogoutSuccessHandler(ReactiveClientRegistrationRepository registrationRepository) {
        OidcClientInitiatedServerLogoutSuccessHandler successHandler = new OidcClientInitiatedServerLogoutSuccessHandler(registrationRepository);
        successHandler.setPostLogoutRedirectUri("http://localhost:8070/");
        return successHandler;
    }
}
