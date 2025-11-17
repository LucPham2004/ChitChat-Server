package com.chitchat.server.controller;

import com.chitchat.server.dto.request.*;
import com.chitchat.server.dto.response.ApiResponse;
import com.chitchat.server.dto.response.UserAuthResponse;
import com.chitchat.server.dto.response.UserDTO;
import com.chitchat.server.dto.response.UserResponse;
import com.chitchat.server.entity.Friendship;
import com.chitchat.server.enums.FriendshipStatus;
import com.chitchat.server.mapper.UserMapper;
import com.chitchat.server.repository.FriendshipRepository;
import com.chitchat.server.service.impl.ConversationServiceImpl;
import com.chitchat.server.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
        private final UserServiceImpl userService;
        private final UserMapper userMapper;
        private final FriendshipRepository friendshipRepository;
        private final ConversationServiceImpl conversationService;

        // POST
        @PostMapping("/create")
        public ApiResponse<UserResponse> createUser(@Valid @RequestBody UserCreationRequest reqUser) {
                var user = this.userService.createUser(reqUser);
                return ApiResponse.<UserResponse>builder()
                                .code(1000)
                                .message("Create user successfully!")
                                .result(userMapper.toUserResponse(user))
                                .build();
        }

        // DELETE
        @DeleteMapping("/delete/{id}")
        public ApiResponse<Void> deleteUserById(@PathVariable String id) {
                this.userService.deleteUserById(id);
                return ApiResponse.<Void>builder()
                                .code(1000)
                                .message("Delete user with ID " + id + " successfully!")
                                .build();
        }

        // GET
        // Get User by id
        @GetMapping("/get/{id}")
        public ApiResponse<UserResponse> getUserById(@PathVariable String id) {
                var dbUser = this.userService.getUser(id);
                return ApiResponse.<UserResponse>builder()
                                .code(1000)
                                .message("Get user with ID " + id + " successfully!")
                                .result(userMapper.toUserResponse(dbUser))
                                .build();
        }

        @GetMapping("/get")
        public ApiResponse<UserResponse> getUserById(
                        @RequestParam String selfId,
                        @RequestParam String otherId) {
                var dbUser = this.userService.getUser(otherId);
                UserResponse userResponse = userMapper.toUserResponse(dbUser);

                if(!Objects.equals(selfId, otherId)) {
                        Friendship friendship = friendshipRepository.findBy2UserIds(selfId, otherId);
                        if(friendship != null) {
                                userResponse.setFriend(friendship.getStatus() == FriendshipStatus.Accepted);
                        } else {
                                userResponse.setFriend(false);
                        }
                }

                userResponse.setConversationId(conversationService.getDirectMessageId(selfId, otherId));

                return ApiResponse.<UserResponse>builder()
                                .code(1000)
                                .message("Get user with ID " + otherId + " successfully!")
                                .result(userResponse)
                                .build();
        }

        // Get User by username or email or phone

        @GetMapping("/get/account")
        public ApiResponse<UserResponse> handleGetAccount(@RequestParam String loginInput) {
                var dbUser = this.userService.handleGetUserByUsernameOrEmailOrPhone(loginInput);
                return ApiResponse.<UserResponse>builder()
                                .code(1000)
                                .message("Get user with login param " + loginInput + " successfully!")
                                .result(userMapper.toUserResponse(dbUser))
                                .build();
        }
        
        @GetMapping("/search")
        public ApiResponse<UserAuthResponse> handleGetUserByLoginInput(@RequestParam String loginInput) {
                var dbUser = this.userService.handleGetUserByLoginInput(loginInput);
                return ApiResponse.<UserAuthResponse>builder()
                                .code(1000)
                                .message("Get user with login param " + loginInput + " successfully!")
                                .result(userMapper.toUserAuthResponse(dbUser))
                                .build();
        }

        @GetMapping("/search&token")
        public ApiResponse<UserAuthResponse> getUserByRefreshTokenAndEmailOrUsernameOrPhone(
                        @RequestParam String refresh_token, 
                        @RequestParam String login) {
                var dbUser = this.userService.getUserByRefreshTokenAndEmailOrUsernameOrPhone(refresh_token, login);
                return ApiResponse.<UserAuthResponse>builder()
                                .code(1000)
                                .message("Get user with login param " + login + " successfully!")
                                .result(userMapper.toUserAuthResponse(dbUser))
                                .build();
        }

        // Get all Users
        @GetMapping("/get/all")
        public ApiResponse<Page<UserDTO>> getAllUsers(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "20") int size) {
                var users = this.userService.getAllUsers(page, size);
                return ApiResponse.<Page<UserDTO>>builder()
                                .code(1000)
                                .message("Get all users successfully!")
                                .result(users.map(userMapper::toUserDTO))
                                .build();
        }

        // Get User's friends
        @GetMapping("/get/friends")
        public ApiResponse<Page<UserDTO>> getUserFriends(
                        @RequestParam String userId,
                        @RequestParam(defaultValue = "0") int pageNum) {
                var friends = this.userService.getUserFriends(userId, pageNum);
                return ApiResponse.<Page<UserDTO>>builder()
                                .code(1000)
                                .message("Get friends of the user with ID " + userId + " successfully!")
                                .result(friends)
                                .build();
        }
        
        // Get User's friend requests
        @GetMapping("/get/friends/request")
        public ApiResponse<Page<UserDTO>> getUserFriendRequests(
                        @RequestParam String userId,
                        @RequestParam(defaultValue = "0") int pageNum) {
                var friends = this.userService.getUserFriendRequests(userId, pageNum);
                return ApiResponse.<Page<UserDTO>>builder()
                                .code(1000)
                                .message("Get friend requests of the user with ID " + userId + " successfully!")
                                .result(friends)
                                .build();
        }

        // Get mutual friends
        @GetMapping("/get/friends/mutual")
        public ApiResponse<Page<UserDTO>> getMutualFriends(
                        @RequestParam String meId,
                        @RequestParam String youId,
                        @RequestParam(defaultValue = "0") int pageNum) {
                var friends = this.userService.getMutualFriends(meId, youId, pageNum);
                return ApiResponse.<Page<UserDTO>>builder()
                                .code(1000)
                                .message("Get mutual friends of user with ID " + meId +
                                                " and user with ID " + youId + " successfully!")
                                .result(friends)
                                .build();
        }

        // Get User's friends
        @GetMapping("/get/friends/suggested")
        public ApiResponse<Page<UserDTO>> getSuggestedFriends(
                        @RequestParam String userId,
                        @RequestParam(defaultValue = "0") int pageNum) {
                var friends = this.userService.getSuggestedFriends(userId, pageNum);
                return ApiResponse.<Page<UserDTO>>builder()
                                .code(1000)
                                .message("Get suggested friends of the user with ID " + userId + " successfully!")
                                .result(friends)
                                .build();
        }

        
        // Get User's friends
        @GetMapping("/search/name")
        public ApiResponse<Page<UserDTO>> searchUsersByName(
                        @RequestParam String userId,
                        @RequestParam String name,
                        @RequestParam(defaultValue = "0") int pageNum) {
                var friends = this.userService.searchUsersByName(userId, name, pageNum);
                return ApiResponse.<Page<UserDTO>>builder()
                                .code(1000)
                                .message("Search users with keyword: " + name + " successfully!")
                                .result(friends)
                                .build();
        }

        // Get User's friends
        @GetMapping("/search/friends")
        public ApiResponse<Page<UserDTO>> searchFriendsByName(
                @RequestParam String userId,
                @RequestParam String name,
                @RequestParam(defaultValue = "0") int pageNum) {
                var friends = this.userService.searchFriendsByName(userId, name, pageNum);
                return ApiResponse.<Page<UserDTO>>builder()
                        .code(1000)
                        .message("Search friends with keyword: " + name + " successfully!")
                        .result(friends)
                        .build();
        }

        @GetMapping("/search-ids")
        public ResponseEntity<List<String>> searchUserIds(
                        @RequestParam String name, 
                        @RequestParam(defaultValue = "0") int pageNum) {
                List<String> userIds = userService.searchUserIds(name, pageNum);
                return ResponseEntity.ok(userIds);
        }

        // PUT
        @PutMapping("/update")
        public ApiResponse<UserResponse> updateUser(@RequestBody UserUpdateInfoRequest reqUser) {
                var user = this.userService.updateUser(reqUser);

                return ApiResponse.<UserResponse>builder()
                                .code(1000)
                                .message("Update user with ID " + reqUser.getId() + " successfully")
                                .result(userMapper.toUserResponse(user))
                                .build();
        }

        @PutMapping("/update/images")
        public ApiResponse<UserResponse> updateUserImages(@RequestBody UserUpdateImageRequest reqUser) {
                var user = this.userService.updateUserImages(reqUser);

                return ApiResponse.<UserResponse>builder()
                                .code(1000)
                                .message("Update user images with ID " + reqUser.getId() + " successfully")
                                .result(userMapper.toUserResponse(user))
                                .build();
        }

        @PutMapping("/update/links")
        public ApiResponse<UserResponse> updateUserLinks(@RequestBody UserUpdateLinksRequest reqUser) {
                var user = this.userService.updateUserLinks(reqUser);

                return ApiResponse.<UserResponse>builder()
                                .code(1000)
                                .message("Update user social links with ID " + reqUser.getId() + " successfully")
                                .result(userMapper.toUserResponse(user))
                                .build();
        }

        @PutMapping("/update/otp")
        public ApiResponse<UserResponse> updateUserOtp(@RequestBody UserUpdateOtpRequest reqUser) {
                var user = this.userService.updateUserOtp(reqUser);

                return ApiResponse.<UserResponse>builder()
                                .code(1000)
                                .message("Update user with ID " + reqUser.getId() + " successfully")
                                .result(userMapper.toUserResponse(user))
                                .build();
        }

        @PutMapping("/update/token")
        public ApiResponse<Void> updateUserToken(
                        @RequestParam String token, 
                        @RequestParam String emailUsernamePhone) {
                this.userService.updateUserToken(token, emailUsernamePhone);

                return ApiResponse.<Void>builder()
                                .code(1000)
                                .message("Update user's token with login value: " + emailUsernamePhone + " successfully")
                                .build();
        }

        @PostMapping("/verify-otp")
        public ApiResponse<Boolean> verifyOtp(@RequestBody String userId, String otp) {
                var isVerified = this.userService.verifyOtp(userId, otp);
                return ApiResponse.<Boolean>builder()
                                .code(1000)
                                .message("Create user successfully!")
                                .result(isVerified)
                                .build();
        }
}
