package com.it52.eventservice.dto.event;

import com.it52.eventservice.model.EventParticipant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class EventResponseDTO {

    private String title;
    private String sub;
    private String authorName;
    private String typePrice;
    private String kind;
    private String status;
    private String description;
    private String place;
    private LocalDateTime startedAt;
    private String titleImage;
    private String titleImageURL;
    private String slug;
    private String externalUrl;
    @Size(max = 10, message = "Можно указать не более 10 тэгов")
    private List<@NotBlank String> tags;
    private AddressDTO address;
    private String addressComment;
    private List<EventParticipant> participants;
}
