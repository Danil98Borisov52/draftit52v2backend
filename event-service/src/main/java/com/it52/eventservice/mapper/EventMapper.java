package com.it52.eventservice.mapper;

import com.it52.eventservice.dto.EventCreateDto;
import com.it52.eventservice.model.Event;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {
    public Event toEntity(EventCreateDto dto) {
        return Event.builder()
                .title(dto.getTitle())
                .imageUrl(dto.getImageUrl())
                .view(dto.getView())
                .type(dto.getType())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .address(dto.getAddress())
                .locationName(dto.getLocationName())
                .externalRegistration(dto.isExternalRegistration())
                .externalUrl(dto.getExternalUrl())
                .tags(dto.getTags())
                .description(dto.getDescription())
                .published(false)
                .approved(false)
                .authorId(dto.getAuthorId())
                .authorName(dto.getAuthorName())
                .build();
    }
}
