package com.it52.notificationservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.it52.notificationservice.dto.EventDto;
import com.it52.notificationservice.util.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Configuration
@EnableKafka
@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private final MailService mailService;
    private final ObjectMapper objectMapper;

    public NotificationService(MailService mailService, ObjectMapper objectMapper) {
        this.mailService = mailService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "event_created", groupId = "notification-group")
    public void listen(String eventJson) {
        try {
            // Десериализация JSON-строки в объект EventDto
            EventDto event = objectMapper.readValue(eventJson, EventDto.class);

            String subject = "Новое мероприятие: " + event.getTitle();

            StringBuilder body = new StringBuilder();
            body.append("Появилось новое мероприятие!\n\n")
                    .append("📌 Название: ").append(event.getTitle()).append("\n")
                    .append("📝 Описание: ").append(event.getDescription()).append("\n")
                    .append("🗓 Дата начала: ").append(event.getStartedAt() != null ? event.getStartedAt() : "не указана").append("\n")
                    .append("📍 Место проведения: ").append(event.getPlace()).append("\n")
                    .append("🏠 Адрес: ").append(event.getAddress()).append("\n");

            if (event.getAddressComment() != null && !event.getAddressComment().isBlank()) {
                body.append("💬 Комментарий к адресу: ").append(event.getAddressComment()).append("\n");
            }

            body.append("👤 Автор: ").append(event.getAuthorName()).append("\n")
                    .append("💰 Участие: ").append(event.getTypePrice()).append("\n")
                    .append("📄 Статус: ").append(event.getStatus()).append("\n")
                    .append("🔗 Ссылка: ").append(event.getExternalUrl() != null ? event.getExternalUrl() : "не указана").append("\n");

            if (event.getTags() != null && !event.getTags().isEmpty()) {
                body.append("🏷 Теги: ").append(String.join(", ", event.getTags())).append("\n");
            }

            logger.info("Sending email to Danil1998borisov1@yandex.ru with subject: {}", subject);
            mailService.sendEmail("Danil1998borisov1@yandex.ru", subject, body.toString());
            logger.info("Email sent successfully");

        } catch (Exception e) {
            logger.error("Failed to process event or send email: {}", e.getMessage(), e);
        }
    }
}


