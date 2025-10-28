package com.chitchat.server.mapper;

import com.chitchat.server.dto.request.UserCreationRequest;
import com.chitchat.server.dto.response.UserAuthResponse;
import com.chitchat.server.dto.response.UserDTO;
import com.chitchat.server.dto.response.UserResponse;
import com.chitchat.server.entity.User;
import com.chitchat.server.enums.Gender;
import com.chitchat.server.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal = true)
public class UserMapper {

    UserRepository userRepository;

    public User toUser(UserCreationRequest request) {
        User user = new User();
        user.setUsername(request.getEmail());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setDob(LocalDate.parse(request.getDob()));
        user.setGender(Objects.equals(request.getGender(), "Male") ? Gender.Male
            : Objects.equals(request.getGender(), "Female") ? Gender.Female
            : Gender.Other);

        return user;
    };

    public UserResponse toUserResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setUsername(user.getUsername());
        userResponse.setEmail(user.getEmail());
        userResponse.setAuthorities(user.getAuthorities());

        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setPhone(user.getPhone());

        userResponse.setAvatarUrl(user.getAvatarUrl() != null ? user.getAvatarUrl() : "/user_default.avif");
        userResponse.setAvatarPublicId(user.getAvatarPublicId());
        userResponse.setCoverPhotoUrl(user.getCoverPhotoUrl() != null ? user.getCoverPhotoUrl() : "https://images.unsplash.com/photo-1501594907352-04cda38ebc29");
        userResponse.setCoverPhotoPublicId(user.getCoverPhotoPublicId());
        
        userResponse.setBio(user.getBio());
        userResponse.setLocation(user.getLocation());
        userResponse.setJob(user.getJob());
        userResponse.setDob(user.getDob());
        userResponse.setCreatedAt(user.getCreatedAt());
        userResponse.setUpdatedAt(user.getUpdatedAt());
        userResponse.setGender(user.getGender());
        userResponse.setFriendNum(userRepository.countFriends(user.getId()));

        userResponse.setFacebook(user.getFacebook());
        userResponse.setTwitter(user.getTwitter());
        userResponse.setInstagram(user.getInstagram());
        userResponse.setLinkedin(user.getLinkedin());
        userResponse.setYoutube(user.getYoutube());
        userResponse.setGithub(user.getGithub());
        userResponse.setTiktok(user.getTiktok());
        userResponse.setDiscord(user.getDiscord());

        return userResponse;
    }

    public UserDTO toUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setAvatarUrl(user.getAvatarUrl() != null ? user.getAvatarUrl() : "/user_default.avif");
        userDTO.setAvatarPublicId(user.getAvatarPublicId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setLocation(user.getLocation());
        userDTO.setJob(user.getJob());
        userDTO.setAvatarUrl(user.getAvatarUrl());
        userDTO.setFriendNum(userRepository.countFriends(user.getId()));

        return userDTO;
    }

    public UserAuthResponse toUserAuthResponse(User user) {
        UserAuthResponse userResponse = new UserAuthResponse();
        userResponse.setId(user.getId());
        userResponse.setUsername(user.getUsername());
        userResponse.setEmail(user.getEmail());
        userResponse.setPhone(user.getPhone());
        userResponse.setPassword(user.getPassword());
        userResponse.setAuthorities(user.getAuthorities());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setLocation(user.getLocation());
        userResponse.setAvatarUrl(user.getAvatarUrl());
        userResponse.setBio(user.getBio());

        return userResponse;
    }

}
