package com.it52.user.keycloak;

import com.it52.user.domain.model.User;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;

import java.util.Map;

@Service
public class KeycloakAdminClientService {

    private final WebClient webClient = WebClient.builder().build();

    public String getAdminToken() {
        Map<String, String> form = Map.of(
                "grant_type", "client_credentials",
                "client_id", "user-service",
                "client_secret", "S4Xk3j3SK9NdVaa14xPiDF28P8sMJY7C"
        );
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.setAll(form);

        return webClient.post()
                .uri("http://keycloak:8080/realms/master/protocol/openid-connect/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(multiValueMap))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(json -> json.get("access_token").asText())
                .block();
    }

    public void createUserInKeycloak(User user) {
        String token = getAdminToken();

        ObjectNode payload = JsonNodeFactory.instance.objectNode();
        payload.put("username", user.getUsername());
        payload.put("email", user.getEmail());
        payload.put("enabled", true);

        ArrayNode credentials = payload.putArray("credentials");
        ObjectNode password = JsonNodeFactory.instance.objectNode();
        //password.put("type", "password");
        //password.put("value", user.getPassword());
        //password.put("temporary", false);
        credentials.add(password);

        webClient.post()
                .uri("http://keycloak:8080/admin/realms/it52/users")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}