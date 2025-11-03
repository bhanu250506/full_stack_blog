package com.example.demo.repository;

import com.example.demo.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage,Long> {

    // Find messages between two users, in either direction
    @Query("SELECT m FROM ChatMessage m WHERE " +
            "(m.sender.id = :userId1 AND m.recipient.id = :userId2) OR " +
            "(m.sender.id = :userId2 AND m.recipient.id = :userId1) " +
            "ORDER BY m.timestamp ASC")
    List<ChatMessage> findConversation(Long userId1, Long userId2);
}
