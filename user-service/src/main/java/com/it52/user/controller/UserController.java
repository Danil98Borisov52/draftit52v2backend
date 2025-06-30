package com.it52.user.controller;

import com.it52.user.dto.UserDTO;
import com.it52.user.dto.UserUpdateDTO;
import com.it52.user.service.api.UserService;
import com.it52.user.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PutMapping(value = "/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDTO> updateCurrentUserProfile(
            @RequestPart("user") UserUpdateDTO userUpdateDTO,
            @RequestPart(value = "avatarImage", required = false) MultipartFile avatarImage) {

        String currentUserSub = userService.getCurrentUserSub();

        if (currentUserSub == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        UserDTO existingUser = userService.updateUser(currentUserSub, userUpdateDTO, avatarImage);
        return ResponseEntity.ok(existingUser);
    }

    @GetMapping("/profile/{sub}")
    public ResponseEntity<UserDTO> getUser(@PathVariable String sub) {
        UserDTO userDTO = userService.getUserBySub(sub);
        if (userDTO != null) {
            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/profile/current")
    public ResponseEntity<UserDTO> getCurrentUserProfile() {
        String userSub  = SecurityUtils.getCurrentUserId();
        if (userSub != null) {
            UserDTO userDTO = userService.getUserBySub(userSub);
            if (userDTO != null) {
                return new ResponseEntity<>(userDTO, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @DeleteMapping("/delete/{sub}")
    public ResponseEntity<Void> deleteUserBySub(@PathVariable String sub) {
        userService.deleteUser(sub);
        return ResponseEntity.noContent().build(); // 204 No Content
    }



}