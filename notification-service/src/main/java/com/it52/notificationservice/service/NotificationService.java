package com.it52.notificationservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.it52.notificationservice.dto.EventDto;
import com.it52.notificationservice.dto.OtpDto;
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
            model.put("title", event.getTitle());
            model.put("description", event.getDescription());
            model.put("authorName", event.getAuthorName());
            model.put("startedAt", formatDate(event.getStartedAt()));
            model.put("address", event.getAddress().getUnrestrictedValue());
            model.put("addressComment", event.getAddressComment());
            model.put("slug", event.getSlug());
            model.put("kind", localizeKind(event.getKind()));
            model.put("typePrice", localizePriceType(event.getTypePrice()));
            model.put("externalUrl", event.getExternalUrl());
            model.put("tags", event.getTags());
            model.put("titleImageCid", "eventBanner");

            String fullUrl = event.getTitleImage();

            int minioUrlStart = fullUrl.indexOf("http://minio");
            if (minioUrlStart == -1) {
                throw new IllegalArgumentException("Minio URL не найден в: " + fullUrl);
            }
            String actualMinioUrl = fullUrl.substring(minioUrlStart);

            URI uri = URI.create(actualMinioUrl);
            String path = uri.getPath();

            if (path.startsWith("/")) {
                path = path.substring(1);
            }

            String[] parts = path.split("/", 2);
            if (parts.length < 2) {
                throw new IllegalArgumentException("Невалидный путь к изображению: " + path);
            }

            String objectName = parts[1];

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
            // 1. Десериализация
            UserRegisteredToEventDto dto = objectMapper.readValue(message, UserRegisteredToEventDto.class);

            // 2. Подготовка данных
            String subject = "Вы зарегистрированы на мероприятие: " + dto.getEventTitle();

            Map<String, Object> model = Map.of(
                    "firstName", dto.getFirstName(),
                    "username", dto.getUsername(),
                    "eventTitle", dto.getEventTitle(),
                    "eventDate", formatDate(dto.getEventDate()),
                    "eventPlace", dto.getEventPlace()
            );

            String templateName = dto.isOrganizer()
                    ? "organizer_registered_to_event.ftl"
                    : "user_registered_to_event.ftl";

            // 3. Генерация письма
            Template template = freemarkerConfig.getTemplate(templateName);
            StringWriter stringWriter = new StringWriter();
            template.process(model, stringWriter);
            String htmlBody = stringWriter.toString();

            // 4. Отправка письма
            logger.info("📨 Отправка email на {} по событию '{}'", dto.getEmail(), dto.getEventTitle());
            mailService.sendHtmlEmail(dto.getEmail(), subject, htmlBody);
            logger.info("✅ Email отправлен успешно");

        } catch (Exception e) {
            logger.error("❌ Ошибка обработки события регистрации: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "otp-email-topic", groupId = "notification-group")
    public void listenOtp(String message) {
        try {
            OtpDto dto = objectMapper.readValue(message, OtpDto.class);

            String subject = "Ваш OTP код";
            Map<String, Object> model = Map.of(
                    "otp", dto.getOtp(),
                    "expiresAt", formatDate(dto.getExpiresAt())
            );

            Template template = freemarkerConfig.getTemplate("otp_email.ftl");
            StringWriter writer = new StringWriter();
            template.process(model, writer);
            String htmlBody = writer.toString();

            logger.info("📨 Отправка OTP на email: {}", dto.getEmail());
            mailService.sendHtmlEmail(dto.getEmail(), subject, htmlBody);
            logger.info("✅ OTP отправлен успешно");

        } catch (Exception e) {
            logger.error("❌ Ошибка отправки OTP: {}", e.getMessage(), e);
        }
    }

    private String formatDate(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) : "не указана";
    }

    private String localizeKind(String kind) {
        return switch (kind) {
            case "MEETUP" -> "Встреча";
            case "EDUCATION" -> "Образовательное мероприятие";
            default -> "Неизвестный тип";
        };
    }

    private String localizePriceType(String priceType) {
        return switch (priceType) {
            case "FREE" -> "Бесплатное";
            case "PAID" -> "Платное";
            default -> "Неизвестный тип оплаты";
        };
    }
}


