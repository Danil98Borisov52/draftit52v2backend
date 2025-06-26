package com.it52.user.service.api;

import com.it52.user.model.User;
import org.springframework.web.multipart.MultipartFile;

public interface UserImageService {
    void uploadUserImageIfPresent(MultipartFile image, User user);
}
