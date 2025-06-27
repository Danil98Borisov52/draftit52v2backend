package com.it52.eventservice.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserChangeRequestDTO {
    private String sub;
    private String avatarImage;
    private boolean anonymous;
}
