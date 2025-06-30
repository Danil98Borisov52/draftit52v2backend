package com.it52.user.service.impl;

import com.it52.user.dto.UserDTO;
import com.it52.user.dto.UserUpdateDTO;
import com.it52.user.exception.UserNotFoundException;
import com.it52.user.kafka.KafkaProducer;
import com.it52.user.model.User;
import com.it52.user.repository.UserRepository;
import com.it52.user.service.api.*;
import com.it52.user.utils.UserMapper;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final KafkaProducer kafkaProducer;
    private final KeycloakService keycloakService;
    private final UserMapper userMapper;
    private final UserImageService userImageService;

    @Value("${minio.bucket}")
    private String bucket;

    @Override
    public UserDTO getUserBySub(String sub) {
        User user = userRepository.findBySub(sub)
                .orElseThrow(() -> new UserNotFoundException(Long.parseLong(sub)));
        ;
        UserDTO userDTO = userMapper.toDto(user);
        return userDTO;
    }

    @Override
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

    @Override
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
                    User newUser = User.builder()
                            .sub(sub)
                            .email(email)
                            .firstName(firstName)
                            .lastName(lastName)
                            .username(username)
                            .bio(bio)
                            .avatarImage(avatarImage)
                            .slug(slug != null ? slug : username)
                            .website(website)
                            .subscription(subscription != null ? subscription : false)
                            .employment(employment)
                            .role(0)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .anonymous(false)
                            .build();

                    kafkaProducer.sendNewUserEvent(newUser);
                    return userRepository.save(newUser);
                });
        return user;
    }

    @Override
    public User saveUser(User user) {
        kafkaProducer.sendUserChanges(user);
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(String sub) {
        User user = userRepository.findBySub(sub)
                .orElseThrow(() -> new UserNotFoundException(Long.parseLong(sub)));
        keycloakService.deleteUserInKeycloak(sub);
        userRepository.delete(user);
    }

    @Override
    public UserDTO updateUser(String currentUserSub, UserUpdateDTO userUpdateDTO, MultipartFile avatarImage) {
        User existingUser = userRepository.findBySub(currentUserSub)
                .orElseThrow(() -> new UserNotFoundException(Long.parseLong(currentUserSub)));
        for (Field field : UserUpdateDTO.class.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object newValue = field.get(userUpdateDTO);
                if (newValue != null) {
                    String setter = "set" + capitalize(field.getName());
                    Method method = User.class.getMethod(setter, field.getType());
                    method.invoke(existingUser, newValue);
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        userImageService.uploadUserImageIfPresent(avatarImage, existingUser);

        return userMapper.toDto(saveUser(existingUser));
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
