package com.example.demo.dto;


import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Builder

public class ProfileResponse {
    private Long id;
    private String name;
    private String avatarUrl;
    private int followerCount;
    private int followingCount;
    private Set<Long> followingIds;
    private Set<Long> followerIds; // ✅ Add this

}
