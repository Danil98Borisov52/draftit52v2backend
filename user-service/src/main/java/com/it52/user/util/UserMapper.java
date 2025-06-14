package com.it52.user.util;

import com.it52.user.dto.UserDTO;
import com.it52.user.dto.UserUpdateDTO;
import com.it52.user.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final ModelMapper modelMapper;

    public UserMapper() {
        this.modelMapper = new ModelMapper();
    }

    public UserDTO toDto(User user) {
        return modelMapper.map(user, UserDTO.class);
    }
}