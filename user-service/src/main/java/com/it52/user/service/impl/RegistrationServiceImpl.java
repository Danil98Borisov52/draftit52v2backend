package com.it52.user.service.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.it52.user.dto.UserDTO;
import com.it52.user.model.User;
import com.it52.user.repository.UserRepository;
import com.it52.user.service.api.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class RegistrationServiceImpl implements RegistrationService {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationServiceImpl.class);

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "user_registered", groupId = "user-group")
    public void listenUser(String userJson) {
        try {
            UserDTO userDto = objectMapper.readValue(userJson, UserDTO.class);

            userRepository.findByEmail(userDto.getEmail()).orElseGet(() -> {
                User newUser = User.builder()
                        .email(userDto.getEmail())
                        .username(userDto.getUsername() != null ? userDto.getUsername() : "user_" + UUID.randomUUID())
                        .firstName(userDto.getFirstName() != null ? userDto.getFirstName() : "")
                        .lastName(userDto.getLastName() != null ? userDto.getLastName() : "")
                        .role(userDto.getRole() != null ? userDto.getRole() : 0)
                        .bio(userDto.getBio() != null ? userDto.getBio() : "")
                        .avatarImage(userDto.getAvatarImage())
                        .slug(userDto.getSlug() != null ? userDto.getSlug() : createUserSlug(userDto.getUsername()))
                        .website(userDto.getWebsite())
                        .subscription(userDto.getSubscription() != null ? userDto.getSubscription() : false)
                        .employment(userDto.getEmployment())
                        .anonymous(userDto.isAnonymous())
                        .sub(userDto.getUsername())
                        .signInCount(0)
                        .build();

                return userRepository.save(newUser);
            });

        } catch (JsonProcessingException e) {
            logger.error("Failed to parse user registration message: {}", userJson, e);
        } catch (DataAccessException e) {
            logger.error("Database error while processing user registration for email {}: {}", userJson, e);
        } catch (Exception e) {
            logger.error("Unexpected error during user registration processing: {}", e.getMessage(), e);
        }
    }

    public static String createUserSlug(String username) {
        if (username == null || username.isBlank()) {
            return "user-" + UUID.randomUUID().toString().substring(0, 8);
        }

        return username
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\s]", "") // убираем всё кроме латинских символов, цифр и пробелов
                .trim()
                .replaceAll("\\s+", "-");
    }
}
