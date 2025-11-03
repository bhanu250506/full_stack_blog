package com.example.demo.controller;


import com.example.demo.dto.GroupRequest;
import com.example.demo.dto.GroupResponse;
import com.example.demo.dto.PostResponse;
import com.example.demo.model.User;
import com.example.demo.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable; // ✅ CORRECT importimport java.util.List;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {


    private final GroupService groupService;


    @PostMapping
    public ResponseEntity<GroupResponse> createGroup(
            @Valid @RequestBody GroupRequest request,
            @AuthenticationPrincipal User currentUser
            ){
        GroupResponse group = groupService.createGroup(request, currentUser);
        return new ResponseEntity<>(group, HttpStatus.CREATED);
    }

    @PostMapping("/{groupId}/join")
    public ResponseEntity<Void> joinGroup(
            @PathVariable Long groupId,
            @AuthenticationPrincipal User currentUser) {
        groupService.joinGroup(groupId, currentUser);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<GroupResponse>> getAllGroups() {
        return ResponseEntity.ok(groupService.getAllGroups());
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<GroupResponse> getGroupById(
            @PathVariable Long groupId,
            @AuthenticationPrincipal User currentUser) { // Pass user to check membership status
        return ResponseEntity.ok(groupService.getGroupById(groupId, currentUser));
    }

    // Get all posts for a specific group
    @GetMapping("/{groupId}/posts")
    public ResponseEntity<List<PostResponse>> getPostsForGroup(
            @PathVariable Long groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = (Pageable) PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(groupService.getPostsForGroup(groupId, (org.springframework.data.domain.Pageable) pageable));
    }
}
