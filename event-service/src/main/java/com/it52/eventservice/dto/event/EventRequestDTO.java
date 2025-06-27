package com.it52.eventservice.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventRequestDTO {

    @NotBlank(message = "Название обязательно")
    private String title;

    @NotNull(message = "Дата начала обязательна")
    private LocalDateTime startedAt;

    @NotBlank(message = "Описание обязательно")
    private String description;

    @NotBlank(message = "Адрес обязателен")
    private String place;

    @Pattern(regexp = "(^$)|(https?://.+)", message = "Некорректный внешний URL")
    private String foreignLink;

    @NotNull(message = "Укажите тип мероприятия")
    private String kind;

    @NotNull(message = "Укажите тип участия в мероприятии")
    private String typePrice;

    @NotNull(message = "Координаты обязательны")
    @Size(min = 2, max = 2, message = "Должны быть указаны широта и долгота")
    private List<@NotNull Double> coords;

    private String addressComment;

    @Size(max = 10, message = "Можно указать не более 10 тэгов")
    private List<@NotBlank String> tags;
}