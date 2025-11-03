package com.example.demo.controller;


import com.example.demo.dto.ChatMessageRequest;
import com.example.demo.dto.ChatMessageResponse;
import com.example.demo.model.ChatMessage;
import com.example.demo.model.User;
import com.example.demo.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


import com.example.demo.dto.ChatMessageRequest;
import com.example.demo.dto.ChatMessageResponse;
import com.example.demo.model.ChatMessage;
import com.example.demo.model.User;
import com.example.demo.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Optional: for logging
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal; // Import Principal
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j // Optional: for logging
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageRequest request, Principal principal) { // Use Principal
        if (principal == null || principal.getName() == null) {
            log.error("Principal or principal name is null in sendMessage. Cannot send message.");
            // Optionally, send an error back to the sender via WebSocket
            return;
        }
        String senderEmail = principal.getName(); // Get email from Principal

        try {
            // 1. Save the message to the database (pass email string)
            ChatMessage savedMessage = chatService.savedMessage(request, senderEmail);
            ChatMessageResponse response = chatService.mapToResponse(savedMessage);

            // 2. Send the message to the recipient's private queue
            messagingTemplate.convertAndSendToUser(
                    request.getRecipientEmail(),
                    "/queue/messages",
                    response
            );
            log.debug("Sent message ID {} to recipient {}", savedMessage.getId(), request.getRecipientEmail());


            // 3. Send the message back to the sender's queue
            messagingTemplate.convertAndSendToUser(
                    senderEmail, // Use the email string here too
                    "/queue/messages",
                    response
            );
            log.debug("Sent message ID {} back to sender {}", savedMessage.getId(), senderEmail);

        } catch (Exception e) {
            log.error("Error processing chat message from {} to {}: {}", senderEmail, request.getRecipientEmail(), e.getMessage(), e);
            // Optionally, inform the sender about the error via WebSocket
            messagingTemplate.convertAndSendToUser(
                    senderEmail,
                    "/queue/errors", // Send to a specific error queue
                    "Error sending message: " + e.getMessage()
            );
        }
    }

    // getChatHistory method uses @AuthenticationPrincipal User for convenience
    @GetMapping("/api/chat/history/{otherUserEmail}")
    @ResponseBody
    public List<ChatMessageResponse> getChatHistory(
            @PathVariable String otherUserEmail,
            @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            log.warn("Attempt to get chat history without authentication for user {}", otherUserEmail);
            // Depending on your security config, this might not be reachable,
            // but it's good practice to handle it.
            // You could throw an AuthenticationException or return an empty list/error response.
            throw new IllegalStateException("User must be authenticated to retrieve chat history.");
        }
        return chatService.getConversation(otherUserEmail, currentUser);
    }
}