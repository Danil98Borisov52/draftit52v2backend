package com.it52.user.service.impl;

import com.it52.user.service.api.KeycloakService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakServiceImpl implements KeycloakService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${keycloak.auth-server-url}")
    private String keycloakUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    /**
     * Удаление пользователя из Keycloak по его ID (sub)
     */
    @Override
    public void deleteUserInKeycloak(String userId) {
        String accessToken = getAdminAccessToken();
        if (accessToken == null) {
            log.error("Не удалось получить токен администратора Keycloak");
            return;
        }

        String url = String.format("%s/admin/realms/%s/users/%s", keycloakUrl, realm, userId);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, request, Void.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Пользователь с ID {} успешно удалён из Keycloak", userId);
        } else {
            log.warn("Не удалось удалить пользователя с ID {} из Keycloak. Код: {}", userId, response.getStatusCode());
        }
    }

    /**
     * Получение access_token для администратора
     */
    public String getAdminAccessToken() {
        String tokenUrl = String.format("%s/realms/master/protocol/openid-connect/token", keycloakUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "grant_type=client_credentials" +
                "&client_id=" + clientId +
                "&client_secret=" + clientSecret;

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
            return (String) response.getBody().get("access_token");
        } catch (Exception e) {
            log.error("Ошибка при получении admin токена Keycloak: {}", e.getMessage(), e);
            return null;
        }
    }
}