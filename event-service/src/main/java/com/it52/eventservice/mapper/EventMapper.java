package com.it52.eventservice.mapper;

import com.it52.eventservice.dto.event.AddressDTO;
import com.it52.eventservice.dto.event.EventRequestDTO;
import com.it52.eventservice.dto.event.EventResponseDTO;
import com.it52.eventservice.model.Address;
import com.it52.eventservice.model.Event;
import com.it52.eventservice.enums.EventKind;
import com.it52.eventservice.enums.EventPriceType;
import com.it52.eventservice.enums.EventStatus;
import com.it52.eventservice.model.EventParticipant;
import com.it52.eventservice.util.EnumConverter;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static com.it52.eventservice.util.SlugUtil.createSlug;

@RequiredArgsConstructor
@Component
public class EventMapper {

    private static final String IMAGE_PROXY_BASE_URL = "http://localhost:8089";

    public Event create(EventRequestDTO dto) {
        return Event.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .startedAt(dto.getStartedAt())
                .place(dto.getPlace())
                .slug(createSlug(dto.getStartedAt(), dto.getTitle()))
                .foreignLink(dto.getForeignLink())
                .kind(EnumConverter.toOrdinal(EventKind.class, dto.getKind()))
                .addressComment(dto.getAddressComment())
                .typePriceId(EnumConverter.toOrdinal(EventPriceType.class, dto.getTypePrice()))
                .published(false)
                .build();
    }

    public static EventResponseDTO toDto(Event event, List<String> tags, String authorName, List<EventParticipant> eventParticipants, Address address, MinioClient minioClient) {
        return EventResponseDTO.builder()
                .title(event.getTitle())
                .description(event.getDescription())
                .place(event.getPlace())
                .sub(event.getAuthor()!=null ? event.getAuthor().getSub() : "Sub вообще не было, даже в другой таблице")
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
                .titleImageURL(event.getTitleImage())
                .slug(createSlug(event.getStartedAt(), event.getTitle()))
                .externalUrl(event.getForeignLink())
                .address(convertAddress(address))
                .addressComment(event.getAddressComment())
                .titleImage(getTitleImage(event.getTitleImage(), 600, 400))
                .participants(eventParticipants)
                .build();
    }

    private static AddressDTO convertAddress(Address address) {
        if (address == null) {
            return null;
        }

        AddressDTO dto = new AddressDTO();
        dto.setUnrestrictedValue(address.getUnrestrictedValue());
        dto.setCity(address.getCity());
        dto.setStreet(address.getStreet());
        dto.setHouse(address.getHouse());
        return dto;
    }

    private static String getTitleImage(String originalImageUrl, Integer width, Integer height) {
        if (originalImageUrl == null || originalImageUrl.isBlank()) {
            return null;
        }
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(IMAGE_PROXY_BASE_URL).append("/").append(originalImageUrl);

            boolean hasParams = originalImageUrl.contains("?");
            if (width != null) {
                sb.append(hasParams ? "&" : "?").append("w=").append(width);
                hasParams = true;
            }
            if (height != null) {
                sb.append(hasParams ? "&" : "?").append("h=").append(height);
            }
            return sb.toString();
        } catch (Exception e) {
            System.err.println("Ошибка формирования URL для imageproxy: " + e.getMessage());
            return originalImageUrl; // fallback
        }
    }

    private static String getStatus(LocalDateTime startedAt) {
        return LocalDateTime.now().isBefore(startedAt)
                ? EventStatus.FUTURE.name()
                : EventStatus.PAST.name();
    }
}
