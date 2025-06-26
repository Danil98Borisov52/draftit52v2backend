package com.it52.eventservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventDto {

    @NotBlank(message = "Название обязательно")
    private String title;

    @NotNull(message = "Дата начала обязательна")
    private LocalDateTime startedAt;

    @NotBlank(message = "Описание обязательно")
    private String description;

    @NotBlank(message = "Ссылка на картинку обязательна")
    private String titleImage;

    @NotBlank(message = "Адрес обязателен")
    private String place;

    @Pattern(regexp = "(^$)|(https?://.+)", message = "Некорректный внешний URL")
    private String foreignLink;

    @NotNull(message = "Укажите тип мероприятия")
    private String kind;

    private Long addressId;

    private String addressComment;

    @NotNull(message = "Укажите тип участия в мероприятии")
    private String typePrice;

    @Size(max = 10, message = "Можно указать не более 10 тэгов")
    private List<@NotBlank String> tags;
}