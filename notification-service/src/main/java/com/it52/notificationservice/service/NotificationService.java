package com.it52.notificationservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.it52.notificationservice.dto.EventDto;
import com.it52.notificationservice.dto.UserDto;
import com.it52.notificationservice.dto.UserRegisteredToEventDto;
import com.it52.notificationservice.util.MailService;
import freemarker.template.Template;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
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
    private final MinioClient minioClient;
    private final String bucket;

    public NotificationService(MailService mailService,
                               ObjectMapper objectMapper,
                               freemarker.template.Configuration freemarkerConfig,
                               MinioClient minioClient,
                               @Value("${minio.bucket}") String bucket) {
        this.mailService = mailService;
        this.objectMapper = objectMapper;
        this.freemarkerConfig = freemarkerConfig;
        this.minioClient = minioClient;
        this.bucket = bucket;
    }

    @KafkaListener(topics = "event_created", groupId = "notification-group")
    public void listen(String eventJson) {
        try {
            EventDto event = objectMapper.readValue(eventJson, EventDto.class);
            String subject = "Новое мероприятие: " + event.getTitle();

            Map<String, Object> model = new HashMap<>();
            model.put("createdAt", formatDate(event.getCreatedAt()));
            model.put("updatedAt", formatDate(event.getUpdatedAt()));
            model.put("published", event.isPublished());
            model.put("slug", event.getSlug());
            model.put("kind", event.getKind());
            model.put("foreignLink", event.getForeignLink());
            model.put("pageviews", event.getPageviews());
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
            model.put("titleImageCid", "eventBanner");

            String fullUrl = event.getTitleImageURL();

            URI uri = URI.create(fullUrl);
            String path = uri.getPath();

            String objectName = path.startsWith("/" + bucket + "/")
                    ? path.substring(bucket.length() + 2)
                    : path.substring(1);

            System.out.println("Объект в бакете: " + objectName);

            try (InputStream imageStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .build())) {

                BufferedImage image = ImageIO.read(imageStream);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, "jpg", baos);
                byte[] imageBytes = baos.toByteArray();

                Template template = freemarkerConfig.getTemplate("event_notification.ftl");
                StringWriter stringWriter = new StringWriter();
                template.process(model, stringWriter);
                String htmlBody = stringWriter.toString();

                logger.info("Sending email with subject: {}", subject);
                mailService.sendHtmlEmailWithInlineImage(
                        "Danil1998borisov1@yandex.ru",
                        subject,
                        htmlBody,
                        imageBytes,
                        "eventBanner",
                        "image/jpeg"
                );

                logger.info("Email sent successfully");
            }

        } catch (Exception e) {
            logger.error("Failed to process event or send email: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "user_registered", groupId = "notification-group")
    public void listenUser(String userJson) {
        try {

            UserDto user = objectMapper.readValue(userJson, UserDto.class);

            String subject = "Добро пожаловать на платформу!";

            Map<String, Object> model = new HashMap<>();
            model.put("firstName", user.getFirstName());
            model.put("username", user.getUsername());

            Template template = freemarkerConfig.getTemplate("welcome_email.ftl");
            StringWriter stringWriter = new StringWriter();
            template.process(model, stringWriter);
            String htmlBody = stringWriter.toString();

            logger.info("Sending welcome email to {} with subject: {}", user.getEmail(), subject);
            mailService.sendHtmlEmail(user.getEmail(), subject, htmlBody);
            logger.info("Welcome email sent successfully");

        } catch (Exception e) {
            logger.error("Failed to process user registration event or send email: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "user_registered_to_event", groupId = "notification-group")
    public void listenUserRegisteredToEvent(String message) {
        try {
            UserRegisteredToEventDto dto = objectMapper.readValue(message, UserRegisteredToEventDto.class);

            String subject = "Вы зарегистрированы на мероприятие: " + dto.getEventTitle();

            Map<String, Object> model = new HashMap<>();
            model.put("firstName", dto.getFirstName());
            model.put("username", dto.getUsername());
            model.put("eventTitle", dto.getEventTitle());
            model.put("eventDate", formatDate(dto.getEventDate()));
            model.put("eventPlace", dto.getEventPlace());

            String templateName = dto.isOrganizer()
                    ? "organizer_registered_to_event.ftl"
                    : "user_registered_to_event.ftl";

            Template template = freemarkerConfig.getTemplate(templateName);
            StringWriter stringWriter = new StringWriter();
            template.process(model, stringWriter);
            String htmlBody = stringWriter.toString();

            logger.info("Sending event registration email to {} for event {}", dto.getEmail(), dto.getEventTitle());
            mailService.sendHtmlEmail(dto.getEmail(), subject, htmlBody);
            logger.info("Event registration email sent successfully");

        } catch (Exception e) {
            logger.error("Failed to process user event registration or send email: {}", e.getMessage(), e);
        }
    }

    private String formatDate(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) : "не указана";
    }
}


