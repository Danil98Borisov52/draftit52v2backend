package com.it52.user.controller;

import com.it52.user.dto.UserDTO;
import com.it52.user.dto.UserUpdateDTO;
import com.it52.user.model.User;
import com.it52.user.service.api.EventRegistrationServiceClient;
import com.it52.user.service.api.UserService;
import com.it52.user.utils.SecurityUtils;
import com.it52.user.utils.UserMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final EventRegistrationServiceClient eventRegistrationServiceClient;

    public UserController(UserService userService, UserMapper userMapper, EventRegistrationServiceClient eventRegistrationServiceClient) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.eventRegistrationServiceClient = eventRegistrationServiceClient;
    }

    @PutMapping(value = "/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDTO> updateCurrentUserProfile(
            @RequestPart("user") UserUpdateDTO userUpdateDTO,
            @RequestPart(value = "avatarImage", required = false) MultipartFile avatarImage) {

        String currentUserSub = userService.getCurrentUserSub();

        if (currentUserSub == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User existingUser = userService.getUserBySub(currentUserSub);
        if (existingUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Обновление полей
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

        userService.uploadAvatarIfPresent(avatarImage, existingUser);

        userService.saveUser(existingUser);
        return ResponseEntity.ok(userMapper.toDto(existingUser));
    }

    @GetMapping("/profile/{sub}")
    public ResponseEntity<UserDTO> getUser(@PathVariable String sub) {
        User user = userService.getUserBySub(sub);
        if (user != null) {
            UserDTO userDTO = userMapper.toDto(user);
            userDTO.setUserEventParticipation(eventRegistrationServiceClient.getUserEvents(user.getSub()));
            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/profile/current")
    public ResponseEntity<UserDTO> getCurrentUserProfile() {
        String userSub  = SecurityUtils.getCurrentUserId();
        if (userSub != null) {
            User user = userService.getUserBySub(userSub);
            if (user != null) {
                UserDTO userDTO = userMapper.toDto(user);
                userDTO.setUserEventParticipation(eventRegistrationServiceClient.getUserEvents(user.getSub()));
                return new ResponseEntity<>(userDTO, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // если не найден или не аутентифицирован
    }

    @DeleteMapping("/delete/{sub}")
    public ResponseEntity<Void> deleteUserBySub(@PathVariable String sub) {
        User user = userService.getUserBySub(sub);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        userService.deleteUser(user);
        return ResponseEntity.noContent().build(); // 204 No Content
    }


    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}