package com.it52.user.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserRegisterDTO {

    private String username;
    private String email;

    public UserRegisterDTO(String username, String email) {
        this.username = username;
        this.email = email;
    }

    // Геттеры и сеттеры
}