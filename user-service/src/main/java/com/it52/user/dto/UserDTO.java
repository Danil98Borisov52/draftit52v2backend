package com.it52.user.dto;

import com.it52.user.domain.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
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

    public static UserDTO from(User user) {
        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getSub(),
                user.getUsername(),
                user.getRole(),
                user.getBio(),
                user.getAvatarImage(),
                user.getSlug(),
                user.getWebsite(),
                user.getSubscription(),
                user.getEmployment()
        );
    }
}