package com.example.demo.service;


import com.example.demo.dto.ChatMessageRequest;
import com.example.demo.dto.ChatMessageResponse;
import com.example.demo.model.ChatMessage;
import com.example.demo.model.User;
import com.example.demo.repository.ChatMessageRepository;
import com.example.demo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


import com.example.demo.dto.ChatMessageRequest;
import com.example.demo.dto.ChatMessageResponse;
import com.example.demo.model.ChatMessage;
import com.example.demo.model.User;
import com.example.demo.repository.ChatMessageRepository;
import com.example.demo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Optional: for logging
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j // Optional: for logging
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChatMessage savedMessage(ChatMessageRequest request, String senderEmail) { // Accept email String
        log.debug("Attempting to save message from {} to {}", senderEmail, request.getRecipientEmail());

        // Fetch sender using the passed email string
        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> {
                    log.error("Sender not found with email: {}", senderEmail);
                    return new EntityNotFoundException("Sender not found with email: " + senderEmail);
                });

        User recipient = userRepository.findByEmail(request.getRecipientEmail())
                .orElseThrow(() -> {
                    log.error("Recipient not found with email: {}", request.getRecipientEmail());
                    return new EntityNotFoundException("Recipient not found with email: " + request.getRecipientEmail());
                });

        // Prevent sending messages to self if desired
        if (Objects.equals(sender.getId(), recipient.getId())) {
            log.warn("User {} attempted to send a message to themselves.", senderEmail);
            throw new IllegalArgumentException("Cannot send messages to yourself.");
        }


        ChatMessage message = ChatMessage.builder()
                .content(request.getContent())
                .sender(sender) // Use the managed sender entity fetched by email
                .recipient(recipient)
                .build();

        ChatMessage saved = chatMessageRepository.save(message);
        log.info("Saved message ID {} from {} to {}", saved.getId(), senderEmail, request.getRecipientEmail());
        return saved;
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getConversation(String otherUserEmail, User currentUser) {
        log.debug("Fetching conversation between {} and {}", currentUser.getEmail(), otherUserEmail);
        User otherUser = userRepository.findByEmail(otherUserEmail)
                .orElseThrow(() -> {
                    log.warn("User not found when fetching conversation: {}", otherUserEmail);
                    return new EntityNotFoundException("User not found: " + otherUserEmail);
                });

        List<ChatMessage> messages = chatMessageRepository.findConversation(currentUser.getId(), otherUser.getId());
        log.debug("Found {} messages in conversation between {} and {}", messages.size(), currentUser.getEmail(), otherUserEmail);
        return messages.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ChatMessageResponse mapToResponse(ChatMessage message) {
        // Basic null checks for safety, though sender/recipient should always exist here
        String senderEmail = (message.getSender() != null) ? message.getSender().getEmail() : "Unknown";
        String recipientEmail = (message.getRecipient() != null) ? message.getRecipient().getEmail() : "Unknown";

        return ChatMessageResponse.builder()
                .id(message.getId())
                .content(message.getContent())
                .timestamp(message.getTimestamp())
                .senderEmail(senderEmail)
                .recipientEmail(recipientEmail)
                .build();
    }
}