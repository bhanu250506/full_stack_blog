package com.example.demo.repository;

import com.example.demo.model.Post;
import com.example.demo.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // --- ADD THIS METHOD ---
    // --- REPLACE YOUR OLD QUERY WITH THIS NEW ONE ---
    @Query("SELECT DISTINCT p FROM Post p LEFT JOIN p.tags t WHERE " +
            "p.title LIKE %:query% OR " +
            "p.content LIKE %:query% OR " +
            "t.name LIKE %:query%")
    List<Post> searchByTitleOrContent(String query);
    Page<Post> findByAuthorAndGroupIsNull(User author, Pageable pageable);
    // --- END OF BLOCK ---
    Page<Post> findByGroupIsNull(Pageable pageable);

    Page<Post> findByGroupId(Long groupId, Pageable pageable);
}
