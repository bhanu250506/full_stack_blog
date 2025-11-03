package com.example.demo.controller; // Use your correct package name

import com.example.demo.dto.PostRequest;
import com.example.demo.dto.PostResponse;
import com.example.demo.dto.UpdatePostRequest;
import com.example.demo.model.User;
import com.example.demo.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page; // Import Page
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable; // ✅ CORRECT Pageable import
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

// import java.awt.print.Pageable; // REMOVED WRONG IMPORT
import java.io.IOException;
import java.nio.file.AccessDeniedException; // Keep if needed, but consider Spring's exceptions
import java.util.List;
// import java.util.List; // We are returning Page now

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final ObjectMapper objectMapper;

    // Update Post
    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long id,
            @RequestBody UpdatePostRequest request,
            @AuthenticationPrincipal User currentUser) throws AccessDeniedException { // Consider using Spring Security exceptions
        PostResponse updatedPost = postService.updatePost(id, request, currentUser);
        return ResponseEntity.ok(updatedPost);
    }

    // Delete Post
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) throws AccessDeniedException { // Consider using Spring Security exceptions
        postService.deletePost(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    // Create Post
    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @RequestParam("post") String postRequestStr,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @AuthenticationPrincipal User currentUser) throws IOException {
        PostRequest postRequest = objectMapper.readValue(postRequestStr, PostRequest.class);
        PostResponse createdPost = postService.createPost(postRequest, file, currentUser);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    // Get Single Post
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    // Get All Posts (Paginated) - CORRECTED
    // In PostController.java

    @GetMapping
    public ResponseEntity<Page<PostResponse>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // Call the service, which returns the correct Page<PostResponse> type
        // REMOVED CAST: Page<PostResponse> postsPage = (Page<PostResponse>) postService.getAllPosts(pageable);
        Page<PostResponse> postsPage = postService.getAllPosts(pageable); // No cast needed

        return ResponseEntity.ok(postsPage);
    }

    @GetMapping("/author/{email}")
    public ResponseEntity<List<PostResponse>> getPostsByAuthor(
            @PathVariable String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(postService.getPostsByAuthorEmail(email, pageable));
    }
}