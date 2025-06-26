package com.it52.eventservice.mapper;

import com.it52.eventservice.dto.EventDto;
import com.it52.eventservice.dto.EventResponseDto;
import com.it52.eventservice.model.Event;
import com.it52.eventservice.enums.EventKind;
import com.it52.eventservice.enums.EventPriceType;
import com.it52.eventservice.enums.EventStatus;
import com.it52.eventservice.model.EventParticipant;
import com.it52.eventservice.util.EnumConverter;
import com.it52.eventservice.util.SlugUtil;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

import static com.it52.eventservice.util.SlugUtil.createSlug;

@RequiredArgsConstructor
@Component
public class EventMapper {

    public Event create(EventDto dto) {
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

    public static EventResponseDto toDto(Event event, List<String> tags, String authorName, List<EventParticipant> eventParticipants, MinioClient minioClient) {

        return EventResponseDto.builder()
                .title(event.getTitle())
                .description(event.getDescription())
                .place(event.getPlace())
                .authorId(event.getAuthor()!=null ? event.getAuthor().getSub() : "Sub вообще не было, даже в другой таблице")
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
                .address("MY HOME")
                .addressComment(event.getAddressComment())
                .titleImage(getTitleImage(event.getTitleImage(), minioClient))
                .participants(eventParticipants)
                .build();
    }

    private static String getTitleImage(String titleImage, MinioClient minioClient) {
        if (titleImage == null || titleImage.isBlank()) {
            return null;
        }

        try {
            String bucket = "event-images";
            URI uri = URI.create(titleImage);
            String path = uri.getPath();

            String objectName = path.startsWith("/" + bucket + "/")
                    ? path.substring(bucket.length() + 2)
                    : path.substring(1);

            try (InputStream imageStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .build())) {

                BufferedImage image = ImageIO.read(imageStream);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, "jpg", baos); // можно заменить на "png", если надо
                byte[] imageBytes = baos.toByteArray();

                return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(imageBytes);
            }

        } catch (Exception e) {
            // можно логировать, если нужно
            System.err.println("Ошибка загрузки изображения из MinIO: " + e.getMessage());
            return null;
        }
    }

    private static String getStatus(LocalDateTime startedAt) {
        return LocalDateTime.now().isBefore(startedAt)
                ? EventStatus.FUTURE.name()
                : EventStatus.PAST.name();
    }
}
