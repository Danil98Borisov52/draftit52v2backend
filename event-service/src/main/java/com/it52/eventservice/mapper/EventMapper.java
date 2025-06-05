package com.it52.eventservice.mapper;

import com.it52.eventservice.dto.EventCreateDto;
import com.it52.eventservice.dto.EventResponseDto;
import com.it52.eventservice.model.Event;
import com.it52.eventservice.enums.EventKind;
import com.it52.eventservice.enums.EventPriceType;
import com.it52.eventservice.enums.EventStatus;
import com.it52.eventservice.util.EnumConverter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class EventMapper {

    public Event create(EventCreateDto dto) {
        return Event.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .startedAt(dto.getStartedAt())
                .titleImage(dto.getTitleImage())
                .place(dto.getPlace())
                .slug(createSlug(dto.getStartedAt(), dto.getTitle()))
                .foreignLink(dto.getForeignLink())
                .kind(EnumConverter.toOrdinal(EventKind.class, dto.getKind()))
                .addressId(dto.getAddressId())
                .addressComment(dto.getAddressComment())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .typePriceId(EnumConverter.toOrdinal(EventPriceType.class, dto.getTypePrice()))
                .published(false)
                .build();
    }

    public static EventResponseDto toDto(Event event, List<String> tags, String authorName) {

        return EventResponseDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .place(event.getPlace())
                .authorId(event.getAuthorId())
                .authorName(authorName)
                .typePrice(event.getTypePriceId() != null
                        ? EnumConverter.toName(EventPriceType.class, event.getTypePriceId())
                        : null)
                .kind(event.getKind() != null
                        ? EnumConverter.toName(EventKind.class, event.getKind())
                        : null)
                .tags(tags)
                .status(getStatus(event.getStartedAt()))
                .startedAt(event.getStartedAt())
                .titleImage(event.getTitleImage())
                .slug(createSlug(event.getStartedAt(), event.getTitle()))
                .createdAt(event.getCreatedAt())
                .address("MY HOME")
                .addressComment(event.getAddressComment())
                .build();
    }

    private static String createSlug(LocalDateTime startedAt, String title){
        String datePart = startedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        String titlePart = title
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .trim()
                .replaceAll("\\s+", "-");

        return datePart + "-" + titlePart;
    }

    private static String getStatus(LocalDateTime startedAt) {
        return LocalDateTime.now().isBefore(startedAt)
                ? EventStatus.FUTURE.name()
                : EventStatus.PAST.name();
    }
}
