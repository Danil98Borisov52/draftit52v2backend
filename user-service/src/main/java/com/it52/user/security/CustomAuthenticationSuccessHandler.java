package com.it52.user.security;

import com.it52.user.model.User;
import com.it52.user.kafka.KafkaProducer;
import com.it52.user.repository.UserRepository;
import com.it52.user.service.UserService;
import com.it52.user.util.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService; // логика создания



    public CustomAuthenticationSuccessHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        User user = userService.createUser((OAuth2User) authentication.getPrincipal());

        response.sendRedirect("/api/users/profile/" + user.getSub());
    }
}