package com.it52.user.service.impl;

import com.it52.user.dto.UserUpdateDTO;
import com.it52.user.exception.UserNotFoundException;
import com.it52.user.kafka.KafkaProducer;
import com.it52.user.model.User;
import com.it52.user.repository.UserRepository;
import com.it52.user.service.api.KeycloakService;
import com.it52.user.service.api.UserService;
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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final KafkaProducer kafkaProducer;
    private final KeycloakService keycloakService;
    private final MinioClient minioClient;
    @Value("${minio.bucket}")
    private String bucket;

    @Override
    public User getUserBySub(String sub) {
        return userRepository.findBySub(sub)
                .orElseThrow(() -> new UserNotFoundException(Long.parseLong(sub)));
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
    public void uploadAvatarIfPresent(MultipartFile image, User user) {
        if (image == null || image.isEmpty()) return;

        try {
            String extension = Objects.requireNonNull(image.getOriginalFilename())
                    .substring(image.getOriginalFilename().lastIndexOf('.'));
            String filename = UUID.randomUUID() + extension;

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(filename)
                            .stream(image.getInputStream(), image.getSize(), -1)
                            .contentType(image.getContentType())
                            .build());

            user.setAvatarImage("/" + bucket + "/" + filename); // путь к объекту
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при загрузке аватарки", e);
        }
    }

    @Override
    public String getAvatarBase64(String avatarPath) {
        if (avatarPath == null || avatarPath.isBlank()) return null;

        try {
            String objectName = avatarPath.startsWith("/" + bucket + "/")
                    ? avatarPath.substring(bucket.length() + 2)
                    : avatarPath.substring(1);

            try (InputStream imageStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .build())) {

                BufferedImage image = ImageIO.read(imageStream);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, "jpg", baos); // или png
                byte[] bytes = baos.toByteArray();

                return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(bytes);
            }

        } catch (Exception e) {
            System.err.println("Ошибка получения аватарки: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void updateUserFields(User existingUser, UserUpdateDTO userUpdateDTO){
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
    }
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    @Override
    public User saveUser(User user) {
        kafkaProducer.sendUserChanges(user);
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(User user) {
        keycloakService.deleteUserInKeycloak(user.getSub());
        userRepository.delete(user);
    }
}
