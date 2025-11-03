package com.example.demo.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchResponse {
    private String type; // "USER" or "POST"
    private Long id;
    private String title; // For posts
    private String contentPreview; // For posts
    private String authorName; // For posts
    private String name; // For users
    private String avatarUrl; // For users

}
