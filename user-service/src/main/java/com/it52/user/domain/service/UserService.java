package com.it52.user.domain.service;

import com.it52.user.domain.model.User;
import com.it52.user.dto.UserDTO;
import com.it52.user.exception.UserAlreadyExistsException;
import com.it52.user.exception.UserNotFoundException;
import com.it52.user.kafka.KafkaProducer;
import com.it52.user.keycloak.KeycloakAdminClientService;
import com.it52.user.repository.UserRepository;
import com.it52.user.util.UserMapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final KeycloakAdminClientService keycloakAdminClientService;
    private final UserMapper userMapper;
    private final KafkaProducer kafkaProducer;



    public UserService(UserRepository userRepository, KeycloakAdminClientService keycloakAdminClientService,
                       UserMapper userMapper, KafkaProducer kafkaProducer) {
        this.userRepository = userRepository;
        this.keycloakAdminClientService = keycloakAdminClientService;
        this.userMapper = userMapper;
        this.kafkaProducer = kafkaProducer;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public User getUserBySub(String sub) {
        return userRepository.findBySub(sub)
                .orElseThrow(() -> new UserNotFoundException(Long.parseLong(sub)));
    }

    public UserDTO processOauthUser(OidcUser oidcUser) {
        String email = oidcUser.getEmail();

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email не получен из токена Keycloak. Проверь настройки клиента и realm.");
        }

        return userRepository.findByEmail(email)
                .map(userMapper::toDto)
                .orElseGet(() -> {
                    User user = new User();
                    user.setEmail(email);
                    //user.setName(Optional.ofNullable(oidcUser.getFullName()).orElse("Без имени"));
                    //user.setRole(Role.USER);
                    //user.setCreatedAt(LocalDateTime.now());
                    return userMapper.toDto(userRepository.save(user));
                });
    }

    public UserDTO registerUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UserAlreadyExistsException("username", user.getUsername());
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("email", user.getEmail());
        }
        keycloakAdminClientService.createUserInKeycloak(user);
        User savedUser = userRepository.save(user);
        UserDTO dto = userMapper.toDto(savedUser);

        //kafkaProducer.sendNewUserEvent(dto);

        return dto;
    }

    public void registerIfNotExists(String username, String email, String name) {
        if (!userRepository.existsByUsername(username)) {
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            userRepository.save(user);
        }
    }

    public boolean existsBySub(String sub) {
        return userRepository.findBySub(sub).isPresent();
    }
}