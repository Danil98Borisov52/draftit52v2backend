package com.it52.authservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/vk")
@RequiredArgsConstructor
public class VKAuthController {

    private final String clientId = "YOUR_CLIENT_ID";       // из VK Dev
    private final String clientSecret = "YOUR_CLIENT_SECRET";
    private final String redirectUri = "http://localhost:8080/api/auth/vk/callback";
    private final String apiVersion = "5.131";

    @GetMapping("/login")
    public ResponseEntity<?> redirectToVk() {
        String authUrl = "https://oauth.vk.com/authorize"
                + "?client_id=" + clientId
                + "&display=page"
                + "&redirect_uri=" + redirectUri
                + "&scope=email"
                + "&response_type=code"
                + "&v=" + apiVersion;

        return ResponseEntity.status(302)
                .header(HttpHeaders.LOCATION, authUrl)
                .build();
    }

    @GetMapping("/callback")
    public ResponseEntity<?> callback(@RequestParam String code) {
        RestTemplate restTemplate = new RestTemplate();

        // 1. Получаем access_token
        String tokenUrl = "https://oauth.vk.com/access_token"
                + "?client_id=" + clientId
                + "&client_secret=" + clientSecret
                + "&redirect_uri=" + redirectUri
                + "&code=" + code;

        Map<String, Object> tokenResponse = restTemplate.getForObject(tokenUrl, Map.class);

        if (tokenResponse == null || tokenResponse.containsKey("error")) {
            return ResponseEntity.badRequest().body("Ошибка авторизации VK");
        }

        String accessToken = (String) tokenResponse.get("access_token");
        Integer userId = (Integer) tokenResponse.get("user_id");
        String email = (String) tokenResponse.get("email"); // может вернуться null

        // 2. Запрашиваем данные пользователя
        String userInfoUrl = "https://api.vk.com/method/users.get"
                + "?user_ids=" + userId
                + "&fields=photo_200,first_name,last_name"
                + "&access_token=" + accessToken
                + "&v=" + apiVersion;

        Map<String, Object> userInfoResponse = restTemplate.getForObject(userInfoUrl, Map.class);

        // 3. Создаем JWT для нашего приложения
        // (тут лучше вызвать твой JwtService)
        String jwtToken = "FAKE_JWT"; // jwtService.generateToken(...)

        return ResponseEntity.ok(Map.of(
                "jwt", jwtToken,
                "vk_token", accessToken,
                "vk_user", userInfoResponse
        ));
    }
}