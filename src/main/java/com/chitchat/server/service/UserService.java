package com.chitchat.server.service;

import com.chitchat.server.dto.request.*;
import com.chitchat.server.dto.response.UserDTO;
import com.chitchat.server.entity.User;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.Optional;

public interface UserService {

    Page<UserDTO> getUserFriends(Long userId, int pageNum);

    // Get User's friend requests
    public Page<UserDTO> getUserFriendRequests(Long userId, int pageNum);

    // Get User friends
    public Page<UserDTO> getSuggestedFriends(Long userId, int pageNum);

    // Get mutual friends
    public Page<UserDTO> getMutualFriends(Long meId, Long youId, int pageNum);

    // Search users by name
    public Page<UserDTO> searchUsersByName(Long userId, String name, int pageNum);

    // Search User ids by name
    public List<Long> searchUserIds(String name, int pageNum);

    // Get User by Id
    public Optional<User> findById(Long id);

    public User getUser(Long id);

    // Get all Users
    public Page<User> getAllUsers(int pageNum, int pageSize);

    // POST
    // Create user
    public User createUser(UserCreationRequest request);

    // PUT
    // Edit user info
    public User updateUser(UserUpdateInfoRequest reqUser);

    // Update user avatar and cover photo
    public User updateUserImages(UserUpdateImageRequest reqUser);

    // Update user avatar and cover photo
    public User updateUserLinks(UserUpdateLinksRequest reqUser);

    public User updateUserOtp(UserUpdateOtpRequest reqUser);

    // DELETE
    public void deleteUserById(Long id);
}
