package com.example.demo.service; // Use your package

import com.example.demo.dto.CommentResponse;
import com.example.demo.dto.PostRequest;
import com.example.demo.dto.PostResponse;
import com.example.demo.dto.UpdatePostRequest;
import com.example.demo.model.Group;
import com.example.demo.model.Post;
import com.example.demo.model.Tag;
import com.example.demo.model.User;
import com.example.demo.repository.GroupRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.TagRepository;
import com.example.demo.repository.UserRepository; // Needed for getPostsByAuthorEmail
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Optional: for logging
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException; // Keep if using this specific exception
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j // Optional
public class PostService {

    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final CloudinaryService cloudinaryService;
    private final CommentService commentService; // Ensure this is correctly injected
    private final GroupRepository groupRepository;
    private final UserRepository userRepository; // Added UserRepository

    // --- Standardized Helper Method (Takes TWO arguments) ---
    private PostResponse mapPostToResponse(Post post, boolean isLikedByCurrentUser) {
        if (post == null || post.getAuthor() == null) {
            log.error("Attempted to map a null post or post with null author. Post ID: {}", post != null ? post.getId() : "null");
            // Return a default or throw an error, depending on desired behavior
            return null; // Or throw new IllegalArgumentException("Post or Post Author cannot be null");
        }
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .mediaUrl(post.getMediaUrl())
                .mediaType(post.getMediaType())
                .createdAt(post.getCreatedAt())
                .authorName(post.getAuthor().getName())
                .authorEmail(post.getAuthor().getEmail()) // Include Author Email
                .tags(post.getTags().stream().map(Tag::getName).collect(Collectors.toSet()))
                .likeCount(post.getLikes().size())
                .likedByCurrentUser(isLikedByCurrentUser) // Include Like Status
                .build();
    }

    // ✅ Create Post - Updated Call to mapPostToResponse
    @Transactional
    public PostResponse createPost(PostRequest postRequest, MultipartFile file, User currentUser) {
        // ... (your existing logic for media, tags, group) ...
        Group group = null;
        if (postRequest.getGroupId() != null) {
            group = groupRepository.findById(postRequest.getGroupId())
                    .orElseThrow(() -> new EntityNotFoundException("Group not found with ID: " + postRequest.getGroupId()));
        }

        Set<Tag> tags = new HashSet<>();
        if (postRequest.getTags() != null) {
            for (String tagName : postRequest.getTags()) {
                Tag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> tagRepository.save(Tag.builder().name(tagName).build()));
                tags.add(tag);
            }
        }
        String mediaUrl = null;
        String mediaType = null;
        if (file != null && !file.isEmpty()) {
            mediaUrl = cloudinaryService.uploadFile(file);
            mediaType = file.getContentType() != null && file.getContentType().startsWith("video") ? "VIDEO" : "IMAGE";
        }


        Post post = Post.builder()
                .title(postRequest.getTitle())
                .content(postRequest.getContent())
                .mediaUrl(mediaUrl)
                .mediaType(mediaType)
                .author(currentUser) // Make sure currentUser is managed or re-fetched if needed
                .tags(tags)
                .group(group)
                .build();

        Post savedPost = postRepository.save(post);
        // Call the standardized helper, new posts are not liked by default
        return mapPostToResponse(savedPost, false);
    }

    // ✅ Get Post by ID - Updated Call to mapPostToResponse
    @Transactional(readOnly = true)
    public PostResponse getPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + postId));

        boolean isLikedByCurrentUser = false;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User currentUser = (User) authentication.getPrincipal();
            isLikedByCurrentUser = post.getLikes().stream()
                    .anyMatch(liker -> liker.getId().equals(currentUser.getId()));
        }

        // Call the standardized helper
        PostResponse response = mapPostToResponse(post, isLikedByCurrentUser);

        // Fetch and add comments (ensure getCommentsForPost exists and works)
        List<CommentResponse> comments = commentService.getCommentForPost(postId); // Corrected method name
        response.setComments(comments);
        return response;
    }

    // ✅ Get All Posts (Paginated) - Call to mapPostToResponse is correct
    @Transactional(readOnly = true)
    public Page<PostResponse> getAllPosts(Pageable pageable) {
        Page<Post> postsPage = postRepository.findByGroupIsNull(pageable);
        // Call the standardized helper (false for list view)
        return postsPage.map(post -> mapPostToResponse(post, false));
    }

    // ✅ Get Posts by Author Email - Updated Call to mapPostToResponse
    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByAuthorEmail(String email, Pageable pageable) {
        User author = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));

        Page<Post> posts = postRepository.findByAuthorAndGroupIsNull(author, pageable);
        // Call the standardized helper (false for list view)
        return posts.stream()
                .map(post -> mapPostToResponse(post, false))
                .collect(Collectors.toList());
    }


    // ✅ Update Post - Updated Call to mapPostToResponse
    @Transactional
    public PostResponse updatePost(Long postId, UpdatePostRequest request, User currentUser)
            throws AccessDeniedException { // Or use Spring's AccessDeniedException

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + postId));

        // Authorization check
        if (!Objects.equals(post.getAuthor().getId(), currentUser.getId())) {
            // Prefer Spring Security's exception if using @PreAuthorize etc.
            throw new AccessDeniedException("You are not authorized to update this post.");
        }

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        // Note: Does not update tags or media in this version

        Post updatedPost = postRepository.save(post);

        // Check like status again after update
        boolean isLiked = updatedPost.getLikes().stream()
                .anyMatch(liker -> liker.getId().equals(currentUser.getId()));
        // Call the standardized helper
        return mapPostToResponse(updatedPost, isLiked);
    }

    // ✅ Delete Post
    @Transactional
    public void deletePost(Long postId, User currentUser) throws AccessDeniedException { // Or use Spring's AccessDeniedException
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + postId));

        // Authorization check
        if (!Objects.equals(post.getAuthor().getId(), currentUser.getId())) {
            throw new AccessDeniedException("You are not authorized to delete this post.");
        }

        postRepository.delete(post);
    }
}