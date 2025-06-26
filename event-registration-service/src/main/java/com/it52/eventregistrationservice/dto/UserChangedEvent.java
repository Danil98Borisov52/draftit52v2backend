package com.it52.eventregistrationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserChangedEvent {
    private String sub;
    private String avatarImage;
    private boolean anonymous;
}
