package com.it52.notificationservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

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
    private String place;
    private LocalDateTime publishedAt;
    private String slug;
    private String foreignLink;
    private Integer pageviews;
    private Integer kind;
    private Long addressId;
    private String addressComment;
}