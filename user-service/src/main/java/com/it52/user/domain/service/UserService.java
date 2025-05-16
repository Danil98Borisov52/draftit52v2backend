package com.it52.user.domain.service;

import com.it52.user.domain.model.User;
import com.it52.user.exception.UserAlreadyExistsException;
import com.it52.user.exception.UserNotFoundException;
import com.it52.user.keycloak.KeycloakAdminClientService;
import com.it52.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final KeycloakAdminClientService keycloakAdminClientService;


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,KeycloakAdminClientService keycloakAdminClientService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.keycloakAdminClientService = keycloakAdminClientService;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }
    public User getUserBySub(String sub) {
        return userRepository.findBySub(sub)
                .orElseThrow(() -> new UserNotFoundException(Long.parseLong(sub)));
    }

    public User registerUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UserAlreadyExistsException("username", user.getUsername());
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("email", user.getEmail());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        keycloakAdminClientService.createUserInKeycloak(user);

        return userRepository.save(user);
    }

    public void registerIfNotExists(String username, String email, String name) {
        if (!userRepository.existsByUsername(username)) {
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            userRepository.save(user);
        }
    }
}