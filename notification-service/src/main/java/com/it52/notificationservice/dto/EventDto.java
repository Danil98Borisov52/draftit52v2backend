package com.it52.notificationservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventDto {
    private Long id;
    private String title;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long organizerId;
    private boolean published;
    private String description;
    private LocalDateTime startedAt;
    private String titleImage;
    private String titleImageURL;
    private String place;
    private String address;
    private String authorName;
    private String typePrice;
    private String kind;
    private List<@NotBlank String> tags;
    private LocalDateTime publishedAt;
    private String slug;
    private String foreignLink;
    private Integer pageviews;
    private String status;
    private String externalUrl;
    private Long addressId;
    private String addressComment;
}