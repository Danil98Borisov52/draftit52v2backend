package com.it52.notificationservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventDto {
    private String title;
    private String sub;
    private String authorName;
    private String typePrice;
    private String kind;
    private String description;
    private String place;
    private LocalDateTime startedAt;
    private String titleImage;
    private String slug;
    private String externalUrl;
    @Size(max = 10, message = "Можно указать не более 10 тэгов")
    private List<@NotBlank String> tags;
    private AddressDTO address;
    private String addressComment;
}