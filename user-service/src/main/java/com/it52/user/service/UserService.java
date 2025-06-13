package com.it52.user.service;

import com.it52.user.model.User;
import com.it52.user.exception.UserNotFoundException;
import com.it52.user.kafka.KafkaProducer;
import com.it52.user.repository.UserRepository;
import com.it52.user.util.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KafkaProducer kafkaProducer;

    public User getUserBySub(String sub) {
        return userRepository.findBySub(sub)
                .orElseThrow(() -> new UserNotFoundException(Long.parseLong(sub)));
    }


    public String getCurrentUserSub() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("No authentication found in security context.");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt jwt) {
            return jwt.getClaimAsString("sub");
        }

        throw new IllegalStateException("Principal is not of type Jwt");
    }

    public User createUser(OAuth2User oAuth2User) {

        String sub = oAuth2User.getAttribute("sub");
        String username = oAuth2User.getAttribute("preferred_username");
        String email = oAuth2User.getAttribute("email");
        String firstName = oAuth2User.getAttribute("given_name");
        String lastName = oAuth2User.getAttribute("family_name");

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
                    newUser.setSlug(username);
                    newUser.setWebsite(website);
                    newUser.setSubscription(subscription != null ? subscription : false);
                    newUser.setEmployment(employment);
                    newUser.setCreatedAt(LocalDateTime.now());
                    newUser.setUpdatedAt(LocalDateTime.now());
                    newUser.setSlug(slug);
                    newUser.setRole(0);
                    kafkaProducer.sendNewUserEvent(newUser);
                    return userRepository.save(newUser);
                });
        return user;
    }

    public User saveUser(User user) {
        return userRepository.save(user); // сохраняем или обновляем в базе
    }
}