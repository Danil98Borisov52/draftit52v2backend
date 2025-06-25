package com.it52.user.dto;

import com.it52.user.model.User;
import lombok.*;

import java.util.List;

@Data
public class UserDTO {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String sub;

    private String username;
    private Integer role;
    private String bio;
    private String avatarImage;
    private String slug;
    private String website;
    private Boolean subscription;
    private String employment;
    private List<UserEventParticipationDTO> userEventParticipation;
}