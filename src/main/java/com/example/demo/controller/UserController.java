package com.example.demo.controller;


import com.example.demo.model.User;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    public ResponseEntity<?> uploadAvatar(
            @AuthenticationPrincipal User currentUser,
            @RequestParam("file")MultipartFile file
            ){
        String avatarUrl = userService.uploadAvatar(currentUser, file);
        return ResponseEntity.ok(Map.of("avatarUrl", avatarUrl));
    }
}
