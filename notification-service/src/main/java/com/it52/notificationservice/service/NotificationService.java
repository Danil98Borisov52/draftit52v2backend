package com.it52.notificationservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.it52.notificationservice.dto.EventDto;
import com.it52.notificationservice.util.MailService;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;


@Configuration
@EnableKafka
@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private final MailService mailService;
    private final ObjectMapper objectMapper;
    private final freemarker.template.Configuration freemarkerConfig;

    public NotificationService(MailService mailService, ObjectMapper objectMapper, freemarker.template.Configuration freemarkerConfig) {
        this.mailService = mailService;
        this.objectMapper = objectMapper;
        this.freemarkerConfig = freemarkerConfig;
    }

    @KafkaListener(topics = "event_created", groupId = "notification-group")
    public void listen(String eventJson) {
        try {

            EventDto event = objectMapper.readValue(eventJson, EventDto.class);
            String subject = "Новое мероприятие: " + event.getTitle();

            // Подготовка модели для шаблона
            Map<String, Object> model = new HashMap<>();
            model.put("title", event.getTitle());
            model.put("description", event.getDescription());
            model.put("startedAt", formatDate(event.getStartedAt()));
            model.put("place", event.getPlace());
            model.put("address", event.getAddress());
            model.put("addressComment", event.getAddressComment());
            model.put("authorName", event.getAuthorName());
            model.put("typePrice", event.getTypePrice());
            model.put("status", event.getStatus());
            model.put("externalUrl", event.getExternalUrl());
            model.put("tags", event.getTags());

            // Загрузка и обработка шаблона
            Template template = freemarkerConfig.getTemplate("event_notification.ftl");
            StringWriter stringWriter = new StringWriter();
            template.process(model, stringWriter);
            String htmlBody = stringWriter.toString();

            // Отправка письма с HTML
            logger.info("Sending email to Danil1998borisov1@yandex.ru with subject: {}", subject);
            mailService.sendHtmlEmail("Danil1998borisov1@yandex.ru", subject, htmlBody);
            logger.info("Email sent successfully");

        } catch (Exception e) {
            logger.error("Failed to process event or send email: {}", e.getMessage(), e);
        }
    }

    private String formatDate(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return (date != null)
                ? date.format(formatter)
                : "не указана";
    }
}


