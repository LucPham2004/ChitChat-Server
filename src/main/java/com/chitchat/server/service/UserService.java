package com.chitchat.server.service;

import com.chitchat.server.dto.request.*;
import com.chitchat.server.dto.response.UserDTO;
import com.chitchat.server.entity.User;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.Optional;

public interface UserService {

    Page<UserDTO> getUserFriends(String userId, int pageNum);

    // Get User's friend requests
    public Page<UserDTO> getUserFriendRequests(String userId, int pageNum);

    // Get User friends
    public Page<UserDTO> getSuggestedFriends(String userId, int pageNum);

    // Get mutual friends
    public Page<UserDTO> getMutualFriends(String meId, String youId, int pageNum);

    // Search users by name
    public Page<UserDTO> searchUsersByName(String userId, String name, int pageNum);

    Page<UserDTO> searchFriendsByName(String userId, String name, int pageNum);

    // Search User ids by name
    public List<String> searchUserIds(String name, int pageNum);

    // Get User by Id
    public Optional<User> findById(String id);

    public User getUser(String id);

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
    public void deleteUserById(String id);
}
