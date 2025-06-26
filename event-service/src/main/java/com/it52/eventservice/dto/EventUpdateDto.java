package com.it52.eventservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventUpdateDto {

    private String title;

    private LocalDateTime startedAt;

    private String description;

    private String titleImage;

    private String place;

    @Pattern(regexp = "(^$)|(https?://.+)", message = "Некорректный внешний URL")
    private String foreignLink;

    private String kind;

    private Long addressId;

    private String addressComment;

    private String typePrice;

    @Size(max = 10, message = "Можно указать не более 10 тэгов")
    private List<@NotBlank String> tags;
}