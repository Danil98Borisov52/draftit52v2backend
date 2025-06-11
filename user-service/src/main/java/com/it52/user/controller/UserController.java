package com.it52.user.controller;

import com.it52.user.dto.UserDTO;
import com.it52.user.dto.UserRegisterDTO;
import com.it52.user.domain.model.User;
import com.it52.user.domain.service.UserService;
import org.springframework.http.ResponseEntity;
import com.it52.user.repository.UserRepository;
import com.it52.user.util.UserMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import com.it52.user.domain.model.Message;

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

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserRegisterDTO userRegisterDTO) {
        User user = userMapper.toEntity(userRegisterDTO);
        return new ResponseEntity<>(userService.registerUser(user), HttpStatus.CREATED);
    }

/*    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal OAuth2User principal) {
        String email = principal.getAttribute("email");
        return userRepository.findByEmail(email)
                .map(user -> ResponseEntity.ok(UserDTO.from(user)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
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

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}