package com.example.demo.service;


import com.example.demo.dto.CommentRequest;
import com.example.demo.dto.CommentResponse;
import com.example.demo.model.Comment;
import com.example.demo.model.Post;
import com.example.demo.model.User;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final NotificationService notificationService; // <-- ADD THIS


    @Transactional
    public CommentResponse createComment(Long postId, CommentRequest request, User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + postId));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .author(currentUser)
                .post(post)
                .build();

        Comment savedComment = commentRepository.save(comment);

        // --- ADD THIS BLOCK ---
        // Only send a notification if the commenter is NOT the post author
        if (!Objects.equals(currentUser.getId(), post.getAuthor().getId())) {
            String message = currentUser.getName() + " commented on your post: \"" + post.getTitle() + "\"";
            notificationService.createAndSendNotification(post.getAuthor(), message);
        }
        // --- END OF BLOCK ---

        return mapToResponse(savedComment);
    }


    @Transactional
    public void deleteComment(Long commentId, User currentUser) throws AccessDeniedException {
       Comment comment = commentRepository.findById(commentId)
               .orElseThrow(()-> new EntityNotFoundException("Comment not found"));

       // Check if the user is the author of the comment OR the author of the post
        if (!Objects.equals(comment.getAuthor().getId(), currentUser.getId()) &&
        !Objects.equals(comment.getPost().getAuthor().getId(), currentUser.getId())){
            throw new AccessDeniedException("You are not authorized to delete this comment.");
        }
        commentRepository.delete(comment);
    }


    public List<CommentResponse> getCommentForPost(Long postId){
        if (!postRepository.existsById(postId)){
            throw new EntityNotFoundException("Post not found with ID :"+postId);
        }

        return commentRepository.findByPostId(postId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    private CommentResponse mapToResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .authorName(comment.getAuthor().getName())
                .authorId(comment.getAuthor().getId())
                .authorEmail(comment.getAuthor().getEmail()) // <-- ADD THIS
                .build();
    }
}
