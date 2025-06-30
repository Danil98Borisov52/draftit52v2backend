package com.it52.user.service.impl;

import com.it52.user.model.User;
import com.it52.user.repository.UserRepository;
import com.it52.user.service.api.MinioService;
import com.it52.user.service.api.UserImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class UserImageServiceImpl implements UserImageService {
    private final UserRepository userRepository;
    private final MinioService minioService;

    @Override
    public void uploadUserImageIfPresent(MultipartFile image, User user) {
        if (image != null && !image.isEmpty()) {
            String imageUrl = minioService.uploadFile(image, user.getId().toString());
            user.setAvatarImage(imageUrl);
            userRepository.save(user);
        }
    }
}
