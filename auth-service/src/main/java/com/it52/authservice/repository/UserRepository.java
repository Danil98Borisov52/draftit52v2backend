package com.it52.authservice.repository;

import com.it52.authservice.model.UserRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserRegistration, Long> {
    Optional<UserRegistration> findByUsername(String username);
    Optional<UserRegistration> findByTelegramUsername(String telegramUsername);
}
