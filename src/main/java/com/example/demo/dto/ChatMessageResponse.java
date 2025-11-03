package com.example.demo.dto;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatMessageResponse {
    private Long id;
    private String content;
    private LocalDateTime timestamp;
    private String senderEmail;
    private String recipientEmail;
}
