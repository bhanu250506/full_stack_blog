package com.example.demo.service;


import com.example.demo.dto.GroupRequest;
import com.example.demo.dto.GroupResponse;
import com.example.demo.dto.PostResponse;
import com.example.demo.model.Group;
import com.example.demo.model.GroupMember;
import com.example.demo.model.User;
import com.example.demo.repository.GroupMemberRepository;
import com.example.demo.repository.GroupRepository;
import com.example.demo.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final PostRepository postRepository;
    private final PostService postService;

    @Transactional
    public GroupResponse createGroup(GroupRequest request, User creator){
        Group group = Group.builder()
                .name(request.getName())
                .description(request.getDescription())
                .creator(creator)
                .build();

        Group savedGroup = groupRepository.save(group);

        joinGroup(savedGroup.getId(), creator);
        return mapToGroupResponse(savedGroup);
    }

    @Transactional
    public void joinGroup(Long groupId, User user){
        Group group = groupRepository.findById(groupId)
                .orElseThrow(()-> new EntityNotFoundException("Group not Found"));

        if (groupMemberRepository.existsByGroupAndUser(group, user)){
            throw new IllegalArgumentException("User is already a member of this group");
        }

        GroupMember member = GroupMember.builder()
                .group(group)
                .user(user)
                .build();
        groupMemberRepository.save(member);
    }

    public List<PostResponse> getPostsForGroup(Long groupId, Pageable pageable){
        if (!groupRepository.existsById(groupId)){
            throw new EntityNotFoundException("Group not found");
        }

        return getPostsInTransaction(groupId, pageable);
    }

    @Transactional(readOnly = true)
    public GroupResponse getGroupById(Long groupId, User currentUser) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found"));

        GroupResponse response = mapToGroupResponse(group);
        // We need member count and if the current user is a member
        // (Requires adding methods to GroupMemberRepository - simplified for now)
        // long memberCount = groupMemberRepository.countByGroup(group); // Example
        // boolean isMember = currentUser != null && groupMemberRepository.existsByGroupAndUser(group, currentUser); // Example
        // response.setMemberCount(memberCount); // Add memberCount field to DTO
        // response.setIsMember(isMember); // Add isMember field to DTO
        return response; // Return basic info for now
    }
    // --- END ---

    @Transactional(readOnly = true)
    public List<GroupResponse> getAllGroups() {
        return groupRepository.findAll().stream()
                .map(this::mapToGroupResponse) // Re-use your existing helper
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public List<PostResponse> getPostsInTransaction(Long groupId, Pageable pageable) {
        return postRepository.findByGroupId(groupId, pageable).stream()
                // We can't use postService::mapPostToResponse directly,
                // so we'll have to get the PostService to map it for us
                // Or... let's simplify and just call the service.
                // A bit inefficient, but easier to implement.
                .map(post -> postService.getPostById(post.getId()))
                .collect(Collectors.toList());
    }

    private GroupResponse mapToGroupResponse(Group group){
        return GroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .creatorName(group.getCreator().getName())
                .build();
    }
}
