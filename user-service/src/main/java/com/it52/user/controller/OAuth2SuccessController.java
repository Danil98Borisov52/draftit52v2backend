package com.it52.user.controller;

import com.it52.user.domain.model.User;
import com.it52.user.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.result.view.RedirectView;

@Controller
@RequestMapping("/api/users")
public class OAuth2SuccessController {
    private final UserRepository userRepository;

    public OAuth2SuccessController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/oauth2/success")
    public RedirectView handleSuccess(@AuthenticationPrincipal OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        String username = oAuth2User.getAttribute("preferred_username");

        userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setUsername(username);
            //newUser.setCreatedAt(LocalDateTime.now());
            return userRepository.save(newUser);
        });

        return new RedirectView("/api/users/me");
    }
}
