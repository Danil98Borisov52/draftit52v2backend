package com.it52.authservice.service;

import com.it52.authservice.model.UserRegistration;
import com.it52.authservice.repository.OtpStorage;
import com.it52.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final OtpStorage otpStorage;
    private final EmailService emailService;
    private final TelegramService telegramService;


    public String login(String username, String rawPassword) {
        UserRegistration user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (passwordEncoder.matches(rawPassword, user.getPassword())) {
            return jwtService.generateToken(username, user.getEmail(), user.getFirstName(), user.getLastName(), user.getRole());
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }

    public void sendOtp(String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String otp = generateOtp();
        otpStorage.saveOtp(username, otp); // сохранить с TTL (напр. 5 мин)

        // выбираем канал доставки
        if (user.getTelegramId() != null) {
            telegramService.sendMessage(user.getTelegramId(), "Ваш код входа: " + otp);
        } else if (user.getEmail() != null) {
            emailService.sendOtp(user.getEmail(), otp);
        } else {
            throw new RuntimeException("No contact method found for user");
        }
    }

    public String verifyOtpAndLogin(String username, String code) {
        if (!otpStorage.verifyOtp(username, code)) {
            throw new RuntimeException("Invalid OTP");
        }
        UserRegistration user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return jwtService.generateToken(username, user.getEmail(), user.getFirstName(), user.getLastName(), user.getRole());
    }

    private String generateOtp() {
        return String.valueOf((int) (Math.random() * 900_000) + 100_000); // 6-значный код
    }
}

