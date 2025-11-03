package com.example.demo.service;


import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {


    private final CloudinaryService cloudinaryService;
    private final UserRepository userRepository;


    public String uploadAvatar(User user, MultipartFile file){
        String avatarUrl = cloudinaryService.uploadFile(file);
        user.setAvatarUrl(avatarUrl);
        userRepository.save(user);
        return avatarUrl;
    }
}
