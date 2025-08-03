package com.it52.authservice.service;

import com.it52.authservice.model.UserRegistration;
import com.it52.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public String login(String username, String rawPassword) {
        UserRegistration user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (passwordEncoder.matches(rawPassword, user.getPassword())) {
            return jwtService.generateToken(username, user.getEmail(), user.getFirstName(), user.getLastName(), user.getRole());
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }
}

