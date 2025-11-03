package com.example.demo.service;


import com.example.demo.dto.NotificationResponse;
import com.example.demo.model.Notification;
import com.example.demo.model.User;
import com.example.demo.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public void createAndSendNotification(User recipient, String message){
        // 1. Save the notification to the database
        Notification notification = Notification.builder()
                .recipient(recipient)
                .message(message)
                .build();

        Notification savedNotification = notificationRepository.save(notification);

        // 2. Map to the Dto
        NotificationResponse response = NotificationResponse.builder()
                .id(savedNotification.getId())
                .message(savedNotification.getMessage())
                .createdAt(savedNotification.getCreatedAt())
                .isRead(savedNotification.isRead())
                .build();

        // 3. Send the notification to the user's private WebSocket queue
        // This is the *exact* same queue the chat system uses!
        messagingTemplate.convertAndSendToUser(
                recipient.getEmail(), // The user's "username"
                "/queue/messages",    // The destination
                response  // The payload
        );
    }
}
