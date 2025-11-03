package com.example.demo.dto;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentResponse {

    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private String authorName;
    private String authorEmail;
    private Long authorId;
}
