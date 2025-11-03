package com.example.demo.service;


import com.example.demo.dto.SearchResponse;
import com.example.demo.model.Post;
import com.example.demo.model.User;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public List<SearchResponse> searchAll(String query){
        // 1. Search for users
        List<SearchResponse> userResult = userRepository.searchByName(query).stream()
                .map(this::mapUserToSearchResponse)
                .collect(Collectors.toList());

        // 2. Search for posts
        List<SearchResponse> postResult = postRepository.searchByTitleOrContent(query).stream()
                .map(this::mapPostToSearchResponse)
                .collect(Collectors.toList());

        //3. Combine the results
        return Stream.concat(userResult.stream(), postResult.stream())
                .collect(Collectors.toList());
    }

    private SearchResponse mapUserToSearchResponse(User user){
        return SearchResponse.builder()
                .type("USER")
                .id(user.getId())
                .name(user.getName())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }

    private SearchResponse mapPostToSearchResponse(Post post) {
        // Create a short preview of the content
        String preview = post.getContent().length() > 100 ?
                post.getContent().substring(0, 100) + "..." :
                post.getContent();

        return SearchResponse.builder()
                .type("POST")
                .id(post.getId())
                .title(post.getTitle())
                .contentPreview(preview)
                .authorName(post.getAuthor().getName())
                .build();
    }


}
