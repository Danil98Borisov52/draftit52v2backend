package com.it52.user.dto;

import lombok.Data;

@Data
public class UserUpdateDTO {

    private String firstName;
    private String lastName;
    private String bio;
    private String avatarImage;
    private String website;
    private Boolean subscription;
    private String employment;
}