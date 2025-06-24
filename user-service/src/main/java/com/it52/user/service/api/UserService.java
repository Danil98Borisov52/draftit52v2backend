package com.it52.user.service.api;

import com.it52.user.dto.UserDTO;
import com.it52.user.dto.UserUpdateDTO;
import com.it52.user.model.User;
import com.it52.user.exception.UserNotFoundException;
import com.it52.user.kafka.KafkaProducer;
import com.it52.user.repository.UserRepository;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

public interface UserService {
    User getUserBySub(String sub);
    String getCurrentUserSub();
    User createUser(OAuth2User oAuth2User);
    void uploadAvatarIfPresent(MultipartFile image, User user);
    void updateUserFields(User existingUser, UserUpdateDTO userUpdateDTO);
    String getAvatarBase64(String avatarPath);
    User saveUser(User user);
    void deleteUser(User user);
}