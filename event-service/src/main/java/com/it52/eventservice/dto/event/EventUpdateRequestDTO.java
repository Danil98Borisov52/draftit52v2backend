package com.it52.eventservice.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventUpdateRequestDTO {

    private String title;

    private LocalDateTime startedAt;

    private String description;

    private String titleImage;

    private String place;

    @Pattern(regexp = "(^$)|(https?://.+)", message = "Некорректный внешний URL")
    private String foreignLink;

    private String kind;

    @Size(min = 2, max = 2, message = "Должны быть указаны широта и долгота")
    private List<@NotNull Double> coords;

    private String addressComment;

    private String typePrice;

    @Size(max = 10, message = "Можно указать не более 10 тэгов")
    private List<@NotBlank String> tags;
}