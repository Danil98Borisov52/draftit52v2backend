package com.it52.user.utils;

import com.it52.user.config.ImageProxyConfig;
import com.it52.user.config.MinioConfig;
import com.it52.user.dto.UserDTO;
import com.it52.user.model.User;
import com.it52.user.service.api.EventRegistrationServiceClient;
import com.it52.user.service.api.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserMapper {
    private final ImageProxyConfig imageProxyConfig;
    private final EventRegistrationServiceClient eventRegistrationServiceClient;
    private final MinioConfig minioConfig;

    public UserDTO toDto(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .sub(user.getSub())
                .username(user.getUsername())
                .role(user.getRole())
                .bio(user.getBio())
                .avatarImage(getTitleImage(user.getAvatarImage(), 600, 400, minioConfig, imageProxyConfig))
                .slug(user.getSlug())
                .website(user.getWebsite())
                .subscription(user.getSubscription())
                .employment(user.getEmployment())
                .anonymous(user.isAnonymous())
                .userEventParticipation(eventRegistrationServiceClient.getUserEvents(user.getSub()))
                .build();
    }

    private static String getTitleImage(String originalImageUrl, Integer width, Integer height, MinioConfig minioConfig, ImageProxyConfig imageProxyConfig) {
        if (originalImageUrl == null || originalImageUrl.isBlank()) {
            return null;
        }
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(imageProxyConfig.getBaseUrl())
                    .append("/")
                    .append(minioConfig.getUrl())
                    .append("/")
                    .append(minioConfig.getBucket())
                    .append("/")
                    .append(originalImageUrl);

            boolean hasParams = originalImageUrl.contains("?");
            if (width != null) {
                sb.append(hasParams ? "&" : "?").append("w=").append(width);
                hasParams = true;
            }
            if (height != null) {
                sb.append(hasParams ? "&" : "?").append("h=").append(height);
            }
            return sb.toString();
        } catch (Exception e) {
            System.err.println("Ошибка формирования URL для imageproxy: " + e.getMessage());
            return originalImageUrl;
        }
    }
}
