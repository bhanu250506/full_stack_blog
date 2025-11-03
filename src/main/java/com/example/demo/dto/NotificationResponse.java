package com.example.demo.dto;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder

public class NotificationResponse {
    private Long id;
    private String message;
    private LocalDateTime createdAt;
    private boolean isRead;
}
