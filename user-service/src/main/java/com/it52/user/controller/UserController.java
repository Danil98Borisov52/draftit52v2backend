package com.it52.user.controller;

import com.it52.user.dto.UserDTO;
import com.it52.user.dto.UserUpdateDTO;
import com.it52.user.model.User;
import com.it52.user.service.UserService;
import com.it52.user.utils.SecurityUtils;
import org.springframework.http.ResponseEntity;
import com.it52.user.util.UserMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

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
                            // Получаем сеттер для соответствующего поля в User
                            String setterMethodName = "set" + capitalize(field.getName());
                            Method setterMethod = User.class.getMethod(setterMethodName, field.getType());
                            setterMethod.invoke(existingUser, newValue);
                        }
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        // Обрабатываем исключения, если метод или поле недоступны
                        e.printStackTrace();
                    }
                }
                // Сохраняем изменения
                userService.saveUser(existingUser);

                // Возвращаем обновленные данные
                UserDTO userDTO = userMapper.toDto(existingUser);
                return new ResponseEntity<>(userDTO, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Если пользователь не найден
            }
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // Если не аутентифицирован
        }
    }

/*    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserRegisterDTO userRegisterDTO) {
        User user = userMapper.toEntity(userRegisterDTO);
        return new ResponseEntity<>(userService.registerUser(user), HttpStatus.CREATED);
    }*/

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