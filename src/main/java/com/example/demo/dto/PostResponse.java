package com.example.demo.dto;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private String mediaUrl;
    private String mediaType;
    private LocalDateTime createdAt;
    private String authorEmail; // <-- ADD THIS
    private String authorName;
    private Set<String> tags;
    private int likeCount;
    private List<CommentResponse> comments; // <-- ADD THIS LINE

    private boolean likedByCurrentUser;
}
