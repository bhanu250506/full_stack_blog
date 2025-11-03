package com.example.demo.dto;


import lombok.Data;

@Data
public class ChatMessageRequest {
    private String recipientEmail;
    private String content;
}
