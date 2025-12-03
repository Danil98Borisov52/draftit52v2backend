package com.it52.authservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class OtpStorage {

    private final StringRedisTemplate redisTemplate;
    private static final Duration OTP_TTL = Duration.ofMinutes(5);

    public void saveOtp(String username, String otp) {
        String key = buildKey(username);
        redisTemplate.opsForValue().set(key, otp, OTP_TTL);
    }

    public boolean verifyOtp(String username, String code) {
        String key = buildKey(username);
        String savedOtp = redisTemplate.opsForValue().get(key);
        if (savedOtp != null && savedOtp.equals(code)) {
            redisTemplate.delete(key); // удалить после успешной проверки
            return true;
        }
        return false;
    }

    private String buildKey(String username) {
        return "otp:" + username;
    }
}
