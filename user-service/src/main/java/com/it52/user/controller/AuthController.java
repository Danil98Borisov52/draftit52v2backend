package com.it52.user.controller;

import com.it52.user.domain.service.UserService;
import com.it52.user.dto.UserDTO;
import com.it52.user.kafka.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final KafkaProducer kafkaProducer;

    public AuthController(UserService userService, KafkaProducer kafkaProducer) {
        this.userService = userService;
        this.kafkaProducer = kafkaProducer;
    }

    @PostMapping("/success")
    public ResponseEntity<UserDTO> handleOAuthLogin(@AuthenticationPrincipal OidcUser oidcUser) {
        UserDTO savedUser = userService.processOauthUser(oidcUser);
        //kafkaProducer.sendNewUserEvent(savedUser);
        return ResponseEntity.ok(savedUser);
    }
}
