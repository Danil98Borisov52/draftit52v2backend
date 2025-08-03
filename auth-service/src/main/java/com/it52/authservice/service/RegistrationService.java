package com.it52.authservice.service;

import com.it52.authservice.dto.RegisterRequest;
import com.it52.authservice.kafka.KafkaProducer;
import com.it52.authservice.model.UserRegistration;
import com.it52.authservice.repository.UserRepository;
import com.it52.authservice.utils.PasswordUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RegistrationService {

    private final UserRepository userRepository;
    private final PasswordUtil passwordUtil;
    private final BCryptPasswordEncoder passwordEncoder;
    private final KafkaProducer kafkaProducer;

    public RegistrationService(UserRepository userRepository, PasswordUtil passwordUtil, BCryptPasswordEncoder passwordEncoder, KafkaProducer kafkaProducer) {
        this.userRepository = userRepository;
        this.passwordUtil = passwordUtil;
        this.passwordEncoder = passwordEncoder;
        this.kafkaProducer = kafkaProducer;
    }

    public void register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        UserRegistration user = UserRegistration.builder()
                .email(request.getEmail())
                .password(hashedPassword)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getNickname())
                .username(request.getUsername())
                .role(request.getRole())
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);
        kafkaProducer.sendNewUserEvent(user);
    }

    private String generateSlug(String base) {
        return base.toLowerCase().replaceAll("\\s+", "-");
    }
}

