package com.it52.authservice.controller;

import com.it52.authservice.repository.UserRepository;
import com.it52.authservice.service.TelegramService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/telegram")
public class TelegramWebhookController {

    private final UserRepository userRepository;
    private final TelegramService telegramService;

    public TelegramWebhookController(UserRepository userRepository, TelegramService telegramService) {
        this.userRepository = userRepository;
        this.telegramService = telegramService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<?> handleTelegramUpdate(@RequestBody Map<String, Object> update) {
        Map<String, Object> message = (Map<String, Object>) update.get("message");
        if (message != null) {
            Map<String, Object> from = (Map<String, Object>) message.get("from");
            String username = (String) from.get("username");
            Long chatId = ((Number) ((Map<String, Object>) message.get("chat")).get("id")).longValue();

            // Найти пользователя по Telegram username и сохранить chatId
            userRepository.findByTelegramUsername(username).ifPresent(user -> {
                user.setTelegramId(chatId);
                userRepository.save(user);
            });

            telegramService.sendMessage(chatId, "Привязка выполнена! Теперь вы будете получать OTP в Telegram.");
        }

        return ResponseEntity.ok().build();
    }
}
