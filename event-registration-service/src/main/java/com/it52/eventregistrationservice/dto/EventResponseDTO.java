package com.it52.eventregistrationservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class EventResponseDTO {

    @NotBlank(message = "Название обязательно")
    private String title;

    @NotBlank(message = "Пользователь id")
    private String authorId;

    @NotBlank(message = "Название имя пользователя")
    private String authorName;

    @NotNull(message = "Укажите тип учатия в мероприятии")
    private String typePrice;

    @NotNull(message = "Укажите тип мероприятия")
    private String kind;

    @NotNull(message = "Укажите тип мероприятия")
    private String status;

    @NotBlank(message = "Описание обязательно")
    private String description;

    @NotBlank(message = "Название площадки обязательно")
    private String place;

    @NotBlank(message = "Начало мероприятия обязательно")
    private LocalDateTime startedAt;

    private String titleImage;

    @NotBlank(message = "Slug обязательный")
    private String slug;

    @Pattern(regexp = "(^$)|(https?://.+)", message = "Некорректный URL")
    private String externalUrl;

    @Size(max = 10, message = "Можно указать не более 10 тэгов")
    private List<@NotBlank String> tags;

    @NotBlank(message = "Адрес обязателен")
    private AddressDTO address;

    private String addressComment;
}
