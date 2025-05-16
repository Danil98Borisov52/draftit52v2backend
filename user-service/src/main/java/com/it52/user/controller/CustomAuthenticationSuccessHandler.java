package com.it52.user.controller;

import com.it52.user.domain.model.User;
import com.it52.user.repository.UserRepository;
import com.it52.user.domain.service.UserService;
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

    private final UserRepository userRepository;
    private final UserMapper userMapper; // если нужен маппинг
    private final UserService userService; // логика создания
    private final PasswordEncoder passwordEncoder; // если нужно

    public CustomAuthenticationSuccessHandler(UserRepository userRepository,
                                              UserService userService,
                                              UserMapper userMapper,
                                              PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String sub = oAuth2User.getAttribute("sub");
        String username = oAuth2User.getAttribute("preferred_username");
        String email = oAuth2User.getAttribute("email");
        String firstName = oAuth2User.getAttribute("given_name");
        String lastName = oAuth2User.getAttribute("family_name");
        String password = oAuth2User.getAttribute("preferred_username");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setSub(sub);
                    newUser.setEmail(email);
                    newUser.setUsername(username);
                    newUser.setFirstName(firstName);
                    newUser.setLastName(lastName);
                    //newUser.setCreatedAt(LocalDateTime.now());
                    // возможно, и другие поля
                    return userRepository.save(newUser);
                });

        response.sendRedirect("/api/users/profile/" + user.getSub());
    }
}