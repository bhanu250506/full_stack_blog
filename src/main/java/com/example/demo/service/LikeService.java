package com.example.demo.service;


import com.example.demo.model.Post;
import com.example.demo.model.User;
import com.example.demo.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final PostRepository postRepository;
    private final NotificationService notificationService; // <-- ADD THIS

    @Transactional
    public void likePost(Long postId, User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        // Add the like to the set
        post.getLikes().add(currentUser);
        postRepository.save(post);

        // --- ADD THIS NOTIFICATION BLOCK ---
        // Only send a notification if the liker is not the post author
        if (!Objects.equals(post.getAuthor().getId(), currentUser.getId())) {
            String message = currentUser.getName() + " liked your post: \"" + post.getTitle() + "\"";
            notificationService.createAndSendNotification(post.getAuthor(), message);


        }
        // --- END OF BLOCK ---
    }

    @Transactional
    public void unlikePost(Long postId, User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        post.getLikes().remove(currentUser);
        postRepository.save(post);
    }
}
