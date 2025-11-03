package com.example.demo.service; // Use your correct package name

import com.example.demo.dto.ProfileResponse;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Optional for logging
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Objects; // Make sure Objects is imported
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j // Optional
public class FollowService {

    private final UserRepository userRepository;
    private final NotificationService notificationService; // Assuming you added this

    @Transactional
    public void followUser(String emailToFollow, User detachedCurrentUser) { // Renamed param for clarity
        log.debug("User {} attempts to follow {}", detachedCurrentUser.getEmail(), emailToFollow);

        // --- FIX: Re-fetch the currentUser within the transaction ---
        User currentUser = userRepository.findById(detachedCurrentUser.getId())
                .orElseThrow(() -> {
                    log.error("Current user {} not found in database during follow action.", detachedCurrentUser.getEmail());
                    return new EntityNotFoundException("Current authenticated user not found");
                });
        // --- END FIX ---

        User userToFollow = userRepository.findByEmail(emailToFollow)
                .orElseThrow(() -> new EntityNotFoundException("User to follow not found: " + emailToFollow));

        if (Objects.equals(userToFollow.getId(), currentUser.getId())) {
            log.warn("User {} attempted to follow themselves.", currentUser.getEmail());
            throw new IllegalArgumentException("You cannot follow yourself.");
        }

        // Now currentUser.getFollowing() can be safely accessed
        boolean added = currentUser.getFollowing().add(userToFollow);

        if (added) {
            userRepository.save(currentUser); // Save the updated relationship
            log.info("User {} successfully followed {}", currentUser.getEmail(), emailToFollow);

            // Send notification
            String message = currentUser.getName() + " started following you.";
            notificationService.createAndSendNotification(userToFollow, message);
        } else {
            log.info("User {} was already following {}.", currentUser.getEmail(), emailToFollow);
            // Optionally throw an exception or just do nothing if already following
        }
    }

    @Transactional
    public void unfollowUser(String emailToUnfollow, User detachedCurrentUser) { // Renamed param
        log.debug("User {} attempts to unfollow {}", detachedCurrentUser.getEmail(), emailToUnfollow);

        // --- FIX: Re-fetch the currentUser within the transaction ---
        User currentUser = userRepository.findById(detachedCurrentUser.getId())
                .orElseThrow(() -> {
                    log.error("Current user {} not found in database during unfollow action.", detachedCurrentUser.getEmail());
                    return new EntityNotFoundException("Current authenticated user not found");
                });
        // --- END FIX ---

        User userToUnfollow = userRepository.findByEmail(emailToUnfollow)
                .orElseThrow(() -> new EntityNotFoundException("User to unfollow not found: " + emailToUnfollow));

        // Now currentUser.getFollowing() can be safely accessed
        boolean removed = currentUser.getFollowing().remove(userToUnfollow);

        if (removed) {
            userRepository.save(currentUser); // Save the updated relationship
            log.info("User {} successfully unfollowed {}", currentUser.getEmail(), emailToUnfollow);
            // Note: We typically don't send a notification for unfollowing.
        } else {
            log.info("User {} was not following {}.", currentUser.getEmail(), emailToUnfollow);
            // Optionally throw an exception or just do nothing
        }
    }

    @Transactional(readOnly = true)
    public ProfileResponse getProfile(String email) {
        log.debug("Fetching profile for {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + email));

        // Safely access lazy collections within the transaction
        int followerCount = user.getFollowers().size();
        int followingCount = user.getFollowing().size();
        Set<Long> followingIds = user.getFollowing().stream()
                .map(User::getId)
                .collect(Collectors.toSet());
        log.debug("Profile for {}: Followers={}, Following={}", email, followerCount, followingCount);


        return ProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .avatarUrl(user.getAvatarUrl())
                .followerCount(followerCount)
                .followingCount(followingCount)
                .followingIds(followingIds)
                // Add isFollowedByCurrentUser later if needed by modifying this method
                // to accept the *viewer's* User object as well.
                .build();
    }
}