package com.example.demo.controller;


import com.example.demo.dto.ProfileResponse;
import com.example.demo.model.User;
import com.example.demo.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @GetMapping("/{email}")
    public ResponseEntity<ProfileResponse> getProfile(@PathVariable String email){
        return ResponseEntity.ok(followService.getProfile(email));
    }


    @PostMapping("/{email}/follow")
    public ResponseEntity<Void> followUser(
            @PathVariable String email,
            @AuthenticationPrincipal User currentUser
            ){
        followService.followUser(email, currentUser);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/{email}/follow")
    public ResponseEntity<Void> unfollowUser(
            @PathVariable String email,
            @AuthenticationPrincipal User currentUser
    ){
        followService.unfollowUser(email, currentUser);
        return ResponseEntity.ok().build();
    }
}

