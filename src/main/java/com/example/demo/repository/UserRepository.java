package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query("SELECT COUNT(f) FROM User u JOIN u.followers f WHERE u.id = :userId")
    int countFollowersByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(f) FROM User u JOIN u.following f WHERE u.id = :userId")
    int countFollowingByUserId(@Param("userId") Long userId);

    @Query("SELECT f.id FROM User u JOIN u.following f WHERE u.id = :userId")
    Set<Long> findFollowingIds(@Param("userId") Long userId);

    @Query("SELECT f.id FROM User u JOIN u.followers f WHERE u.id = :userId")
    Set<Long> findFollowerIds(@Param("userId") Long userId);



    // --- ADD THIS METHOD ---
    @Query("SELECT u FROM User u WHERE u.name LIKE %:query%")
    List<User> searchByName(String query);
    // --- END OF BLOCK ---
}
