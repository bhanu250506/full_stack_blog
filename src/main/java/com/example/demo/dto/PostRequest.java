package com.example.demo.dto;


import lombok.Data;

import java.util.Set;

@Data
public class PostRequest {
    private String title;
    private String content;
    private Set<String> tags;
    private Long groupId;
}
