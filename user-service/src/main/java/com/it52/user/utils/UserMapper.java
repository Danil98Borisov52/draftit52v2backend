package com.it52.user.utils;

import com.it52.user.dto.UserDTO;
import com.it52.user.model.User;
import com.it52.user.service.api.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final ModelMapper modelMapper;
    private final UserService userService;

    public UserMapper(UserService userService) {
        this.userService = userService;
        this.modelMapper = new ModelMapper();
    }

    public UserDTO toDto(User user) {
        UserDTO dto = modelMapper.map(user, UserDTO.class);
        dto.setAvatarImage(userService.getAvatarBase64(user.getAvatarImage()));
        return dto;
    }
}