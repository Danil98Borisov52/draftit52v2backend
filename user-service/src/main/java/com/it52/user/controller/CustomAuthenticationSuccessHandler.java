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

        // Дополнительно: попытка получить опциональные поля, если они есть в Keycloak
        String bio = oAuth2User.getAttribute("bio");
        String avatarImage = oAuth2User.getAttribute("picture");
        String slug = username != null ? username.toLowerCase().replaceAll("\\s+", "-") : null;
        String website = oAuth2User.getAttribute("website");
        Boolean subscription = oAuth2User.getAttribute("subscription");
        String employment = oAuth2User.getAttribute("employment");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setSub(sub);
                    newUser.setEmail(email);
                    newUser.setFirstName(firstName);
                    newUser.setLastName(lastName);
                    newUser.setUsername(username);
                    newUser.setBio(bio);
                    newUser.setAvatarImage(avatarImage);
                    newUser.setSlug(createSlugUser(username));
                    newUser.setWebsite(website);
                    newUser.setSubscription(subscription != null ? subscription : false);
                    newUser.setEmployment(employment);
                    newUser.setCreatedAt(LocalDateTime.now());
                    newUser.setRole(0); // по умолчанию, например, обычный пользователь
                    return userRepository.save(newUser);
                });

        response.sendRedirect("/api/users/profile/" + user.getSub());
    }

    private String createSlugUser(String username){
        return username;
    }
}