package com.example.demo.model; // Use your package

import jakarta.persistence.*;
import lombok.*; // Import individual annotations
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList; // Import ArrayList
import java.util.HashSet;
import java.util.List; // Import List
import java.util.Set;

// --- Use individual annotations instead of @Data ---
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id") // Crucial for JPA entity equality
// --- End Annotation Fix ---
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob // large object for long text content
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private String mediaUrl;
    private String mediaType; // e.g., "IMAGE", "VIDEO"

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Relationship to Author (User)
    @ManyToOne(fetch = FetchType.LAZY) // Lazy is generally preferred
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    // Relationship to Tags
    @ManyToMany(fetch = FetchType.EAGER /* Or LAZY */, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "post_tags",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default // Ensure initialization
    private Set<Tag> tags = new HashSet<>();

    // Relationship to Likes (Users)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "post_likes",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default // Ensure initialization
    private Set<User> likes = new HashSet<>();

    // Relationship to Group (Optional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = true)
    private Group group;

    // --- ADD THIS RELATIONSHIP FOR COMMENTS (REQUIRED FIX) ---
    @OneToMany(
            mappedBy = "post", // Points to the 'post' field in the Comment entity
            cascade = CascadeType.ALL, // Delete Comments when Post is deleted
            orphanRemoval = true,      // Delete Comments if removed from this list
            fetch = FetchType.LAZY     // Load comments only when needed
    )
    @Builder.Default // Ensure initialization
    private List<Comment> comments = new ArrayList<>();
    // --- END ADDED BLOCK ---

}