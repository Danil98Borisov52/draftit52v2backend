package com.it52.eventservice.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Название
    private String title;

    // Картинка для привлечения внимания
    private String imageUrl;

    // Вид: Конференция, Митап, Обучение
    @Enumerated(EnumType.STRING)
    private EventView view;

    // Тип: Бесплатное, Платное
    @Enumerated(EnumType.STRING)
    private EventType type;

    // Дата начала
    private LocalDateTime startDate;

    // Дата окончания
    private LocalDateTime endDate;

    // Адрес
    private String address;

    // Место проведения (название площадки)
    private String locationName;

    // Чекбокс "Сторонняя регистрация"
    private boolean externalRegistration;

    // Если включена сторонняя регистрация — URL
    private String externalUrl;

    // Теги
    @ElementCollection
    private List<String> tags;

    // Описание
    @Column(columnDefinition = "TEXT")
    private String description;

    // Технические поля (можно скрыть на фронте, но оставить в базе)
    private boolean published;
    private boolean approved;
    private String authorId;
    private String authorName;

    // ✅ Приватный конструктор для Builder
    private Event(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.imageUrl = builder.imageUrl;
        this.view = builder.view;
        this.type = builder.type;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.address = builder.address;
        this.locationName = builder.locationName;
        this.externalRegistration = builder.externalRegistration;
        this.externalUrl = builder.externalUrl;
        this.tags = builder.tags;
        this.description = builder.description;
        this.published = builder.published;
        this.approved = builder.approved;
        this.authorId = builder.authorId;
        this.authorName = builder.authorName;
    }

    public Event() {

    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String title;
        private String imageUrl;
        private EventView view;
        private EventType type;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private String address;
        private String locationName;
        private boolean externalRegistration;
        private String externalUrl;
        private List<String> tags;
        private String description;
        private boolean published;
        private boolean approved;
        private String authorId;
        private String authorName;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Builder view(EventView view) {
            this.view = view;
            return this;
        }

        public Builder type(EventType type) {
            this.type = type;
            return this;
        }

        public Builder startDate(LocalDateTime startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder endDate(LocalDateTime endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder locationName(String locationName) {
            this.locationName = locationName;
            return this;
        }

        public Builder externalRegistration(boolean externalRegistration) {
            this.externalRegistration = externalRegistration;
            return this;
        }

        public Builder externalUrl(String externalUrl) {
            this.externalUrl = externalUrl;
            return this;
        }

        public Builder tags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder published(boolean published) {
            this.published = published;
            return this;
        }

        public Builder approved(boolean approved) {
            this.approved = approved;
            return this;
        }

        public Builder authorId(String authorId) {
            this.authorId = authorId;
            return this;
        }
        public Builder authorName(String authorName) {
            this.authorName = authorName;
            return this;
        }


        public Event build() {
            return new Event(this);
        }

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public EventView getView() {
        return view;
    }

    public void setView(EventView view) {
        this.view = view;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public boolean isExternalRegistration() {
        return externalRegistration;
    }

    public void setExternalRegistration(boolean externalRegistration) {
        this.externalRegistration = externalRegistration;
    }

    public String getExternalUrl() {
        return externalUrl;
    }

    public void setExternalUrl(String externalUrl) {
        this.externalUrl = externalUrl;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
}