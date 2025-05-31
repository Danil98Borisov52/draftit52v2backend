package com.it52.notificationservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.it52.notificationservice.model.Event;
import com.it52.notificationservice.util.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @KafkaListener(topics = "   ", groupId = "notification-group")
    public void listen(String eventJson) {
        try {
            // Десериализация JSON-строки в объект Event
            Event event = objectMapper.readValue(eventJson, Event.class);

            String subject = "New Event: " + event.getTitle();
            String body = "Event Details:\n" +
                    "Title: " + event.getTitle() + "\n" +
                    "Description: " + event.getDescription() + "\n" +
                    "Start Date: " + (event.getStartDate() != null ? event.getStartDate() : "Not specified") + "\n" +
                    "Location: " + event.getLocationName() + " (" + event.getAddress() + ")\n" +
                    "Tags: " + String.join(", ", event.getTags());
            logger.info("Sending email to Danil1998borisov1@yandex.ru with subject: {}", subject);
            mailService.sendEmail("Danil1998borisov1@yandex.ru", subject, body);
            logger.info("Email sent successfully");
        } catch (Exception e) {
            logger.error("Failed to process event or send email: {}", e.getMessage(), e);
        }
    }
}


