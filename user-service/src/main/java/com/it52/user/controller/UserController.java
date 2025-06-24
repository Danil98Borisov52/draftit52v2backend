package com.it52.user.controller;

import com.it52.user.dto.UserDTO;
import com.it52.user.dto.UserUpdateDTO;
import com.it52.user.model.User;
import com.it52.user.service.api.UserService;
import com.it52.user.utils.SecurityUtils;
import com.it52.user.utils.UserMapper;
import org.springframework.http.ResponseEntity;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PutMapping("/edit")
    public ResponseEntity<UserDTO> updateCurrentUserProfile(@RequestBody UserUpdateDTO userUpdateDTO) {
        String currentUserSub = userService.getCurrentUserSub();

        if (currentUserSub != null) {
            User existingUser = userService.getUserBySub(currentUserSub);

            if (existingUser != null) {
                for (Field field : UserUpdateDTO.class.getDeclaredFields()) {
                    field.setAccessible(true);
                    try {
                        Object newValue = field.get(userUpdateDTO);
                        if (newValue != null) {
                            String setterMethodName = "set" + capitalize(field.getName());
                            Method setterMethod = User.class.getMethod(setterMethodName, field.getType());
                            setterMethod.invoke(existingUser, newValue);
                        }
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
                userService.saveUser(existingUser);

                UserDTO userDTO = userMapper.toDto(existingUser);
                return new ResponseEntity<>(userDTO, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Если пользователь не найден
            }
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // Если не аутентифицирован
        }
    }

    @GetMapping("/profile/{sub}")
    public ResponseEntity<UserDTO> getUser(@PathVariable String sub) {
        User user = userService.getUserBySub(sub);
        if (user != null) {
            UserDTO userDTO = userMapper.toDto(user);
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
                return new ResponseEntity<>(userDTO, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // если не найден или не аутентифицирован
    }


    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }


/*    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleNotFound(Exception ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Страница не найдена. Убедитесь, что путь введен правильно.");
    }*/
}