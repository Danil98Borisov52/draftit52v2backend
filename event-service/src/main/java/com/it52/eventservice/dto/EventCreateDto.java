package com.it52.eventservice.dto;

import com.it52.eventservice.model.EventView;
import com.it52.eventservice.model.EventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventCreateDto {

    @NotBlank(message = "Название обязательно")
    private String title;

    @NotBlank(message = "Ссылка на картинку обязательна")
    private String imageUrl;

    @NotNull(message = "Укажите вид мероприятия")
    private EventView view; // Enum: CONFERENCE, MEETUP, TRAINING

    @NotNull(message = "Укажите тип мероприятия")
    private EventType type; // Enum: FREE, PAID

    @NotNull(message = "Дата начала обязательна")
    private LocalDateTime startDate;

    @NotBlank(message = "Название id")
    private String authorId;

    @NotBlank(message = "Название имя пользователя")
    private String authorName;

    @NotNull(message = "Дата окончания обязательна")
    private LocalDateTime endDate;

    @NotBlank(message = "Адрес обязателен")
    private String address;

    @NotBlank(message = "Название площадки обязательно")
    private String locationName;

    private boolean externalRegistration;

    @Pattern(regexp = "(^$)|(https?://.+)", message = "Некорректный URL")
    private String externalUrl;

    @Size(max = 10, message = "Можно указать не более 10 тэгов")
    private List<@NotBlank String> tags;

    @NotBlank(message = "Описание обязательно")
    private String description;

    public EventCreateDto(String title, String imageUrl, EventView view, EventType type, LocalDateTime startDate, LocalDateTime endDate,
                          String address, String locationName, boolean externalRegistration, String externalUrl,
                          List<@NotBlank String> tags, String description, String authorId, String authorName) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.view = view;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.address = address;
        this.locationName = locationName;
        this.externalRegistration = externalRegistration;
        this.externalUrl = externalUrl;
        this.tags = tags;
        this.description = description;
        this.authorId = authorId;
        this.authorName = authorName;
    }

    public @NotBlank(message = "Название обязательно") String getTitle() {
        return title;
    }

    public void setTitle(@NotBlank(message = "Название обязательно") String title) {
        this.title = title;
    }

    public @NotBlank(message = "Ссылка на картинку обязательна") String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(@NotBlank(message = "Ссылка на картинку обязательна") String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public @NotNull(message = "Укажите вид мероприятия") EventView getView() {
        return view;
    }

    public void setView(@NotNull(message = "Укажите вид мероприятия") EventView view) {
        this.view = view;
    }

    public @NotNull(message = "Укажите тип мероприятия") EventType getType() {
        return type;
    }

    public void setType(@NotNull(message = "Укажите тип мероприятия") EventType type) {
        this.type = type;
    }

    public @NotNull(message = "Дата начала обязательна") LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(@NotNull(message = "Дата начала обязательна") LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public @NotNull(message = "Дата окончания обязательна") LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(@NotNull(message = "Дата окончания обязательна") LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public @NotBlank(message = "Адрес обязателен") String getAddress() {
        return address;
    }

    public void setAddress(@NotBlank(message = "Адрес обязателен") String address) {
        this.address = address;
    }

    public @NotBlank(message = "Название площадки обязательно") String getLocationName() {
        return locationName;
    }

    public void setLocationName(@NotBlank(message = "Название площадки обязательно") String locationName) {
        this.locationName = locationName;
    }

    public boolean isExternalRegistration() {
        return externalRegistration;
    }

    public void setExternalRegistration(boolean externalRegistration) {
        this.externalRegistration = externalRegistration;
    }

    public @Pattern(regexp = "(^$)|(https?://.+)", message = "Некорректный URL") String getExternalUrl() {
        return externalUrl;
    }

    public void setExternalUrl(@Pattern(regexp = "(^$)|(https?://.+)", message = "Некорректный URL") String externalUrl) {
        this.externalUrl = externalUrl;
    }

    public @Size(max = 10, message = "Можно указать не более 10 тэгов") List<@NotBlank String> getTags() {
        return tags;
    }

    public void setTags(@Size(max = 10, message = "Можно указать не более 10 тэгов") List<@NotBlank String> tags) {
        this.tags = tags;
    }

    public @NotBlank(message = "Описание обязательно") String getDescription() {
        return description;
    }

    public void setDescription(@NotBlank(message = "Описание обязательно") String description) {
        this.description = description;
    }

    public @NotBlank(message = "Название имя пользователя") String getAuthorName() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return jwt.getClaimAsString("firstName");
    }

    public void setAuthorName(@NotBlank(message = "Название имя пользователя") String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorId() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return jwt.getClaimAsString("sub");
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }
}