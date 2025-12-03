package com.it52.authservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_registration")
public class UserRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "telegram_id")
    private Long telegramId;

    @Column(name = "telegram_username")
    private String telegramUsername;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "first_name", length = 255)
    private String firstName;

    @Column(name = "last_name", length = 255)
    private String lastName;

    @Column(length = 255)
    private String username;

    @Column(nullable = false)
    private Integer role;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}