package com.chitchat.server.service.impl;

import com.chitchat.server.dto.request.*;
import com.chitchat.server.dto.response.UserDTO;
import com.chitchat.server.entity.Friendship;
import com.chitchat.server.entity.Role;
import com.chitchat.server.entity.User;
import com.chitchat.server.enums.FriendshipStatus;
import com.chitchat.server.enums.Gender;
import com.chitchat.server.exception.AppException;
import com.chitchat.server.exception.ErrorCode;
import com.chitchat.server.mapper.UserMapper;
import com.chitchat.server.repository.FriendshipRepository;
import com.chitchat.server.repository.RoleRepository;
import com.chitchat.server.repository.UserRepository;
import com.chitchat.server.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    FriendshipRepository friendshipRepository;
    ConversationServiceImpl conversationService;
    RoleRepository roleRepository;
    PasswordEncoder passwordEncoder;

    static int USERS_PER_PAGE = 20;

    // GET

    // Get User friends
    public Page<UserDTO> getUserFriends(String userId, int pageNum) {
        if (!userRepository.existsByIdAndIsActiveTrue(userId)) {
            throw new AppException(ErrorCode.ENTITY_NOT_EXISTED);
        }
        Pageable pageable = PageRequest.of(pageNum, USERS_PER_PAGE);

        Page<User> friends = userRepository.findFriends(userId, pageable);

        return getUsersWithMutualFriendsCount(userId, friends);
    }
    
    // Get User's friend requests
    public Page<UserDTO> getUserFriendRequests(String userId, int pageNum) {
        if (!userRepository.existsByIdAndIsActiveTrue(userId)) {
            throw new AppException(ErrorCode.ENTITY_NOT_EXISTED);
        }
        Pageable pageable = PageRequest.of(pageNum, USERS_PER_PAGE);

        Page<User> friends = userRepository.findFriendRequests(userId, pageable);

        return getUsersWithMutualFriendsCount(userId, friends);
    }

    // Get User friends
    public Page<UserDTO> getSuggestedFriends(String userId, int pageNum) {
        if (!userRepository.existsByIdAndIsActiveTrue(userId)) {
            throw new AppException(ErrorCode.ENTITY_NOT_EXISTED);
        }
        Pageable pageable = PageRequest.of(pageNum, USERS_PER_PAGE);

        Page<User> friends = userRepository.findSuggestedFriends(userId, pageable);

        if(friends.isEmpty()) {
            friends = userRepository.findRandomUsers(userId, pageable);
        }

        return getUsersWithMutualFriendsCount(userId, friends);
    }

    // Get mutual friends
    public Page<UserDTO> getMutualFriends(String meId, String youId, int pageNum) {
        if (!userRepository.existsByIdAndIsActiveTrue(meId) || !userRepository.existsByIdAndIsActiveTrue(youId)) {
            throw new AppException(ErrorCode.ENTITY_NOT_EXISTED);
        }
        Pageable pageable = PageRequest.of(pageNum, USERS_PER_PAGE);

        Page<User> mutualFriends = userRepository.findMutualFriends(meId, youId, pageable);

        return getUsersWithMutualFriendsCount(meId, mutualFriends);
    }

    // Search users by name
    public Page<UserDTO> searchUsersByName(String userId, String name, int pageNum) {
        Pageable pageable = PageRequest.of(pageNum, USERS_PER_PAGE);

        Page<User> users = userRepository.searchByAnyName(userId, name, pageable);

        return getUsersWithMutualFriendsCount(userId, users);
    }

    // Search User ids by name
    public List<String> searchUserIds(String name, int pageNum) {
        Pageable pageable = PageRequest.of(pageNum, USERS_PER_PAGE);

        Page<User> users = userRepository.searchByAnyName(name, pageable);
        return users.stream().map(User::getId).collect(Collectors.toList());
    }

    // Get User by Id
    public Optional<User> findById(String id) {
        Optional<User> optionalUser = userRepository.findByIdAndIsActiveTrue(id);
        if (optionalUser.isEmpty()) {
            throw new AppException(ErrorCode.ENTITY_NOT_EXISTED);
        }
        return optionalUser;
    }

    public User getUser(String id) {
        return userRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_EXISTED));
    }

    // Get all Users
    public Page<User> getAllUsers(int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);

        return userRepository.findAll(pageable);
    }

    // POST
    // Create user
    @Transactional
    public User createUser(UserCreationRequest request) {
        log.info("request: " + request.toString() + " existsByEmailAndIsActiveTrue: " + userRepository.existsByEmailAndIsActiveTrue(request.getEmail()) + " existsByPhoneAndIsActiveTrue: " + userRepository.existsByPhoneAndIsActiveTrue(request.getPhone()));
        if (userRepository.existsByEmailAndIsActiveTrue(request.getEmail()) && request.getEmail() != null
                || userRepository.existsByPhoneAndIsActiveTrue(request.getPhone()) && request.getPhone() != null) {
                    log.info("this email/phone is already taken: " +
                        request.getEmail() + "/" + request.getPhone());
            throw new AppException(ErrorCode.ACCOUNT_EXISTED);
        }
        User user = userMapper.toUser(request);

        Set<Role> authorities = new HashSet<>();

        Optional<Role> role = roleRepository.findByAuthority("USER");
        role.ifPresent(authorities::add);

        user.setAuthorities(authorities);
        
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        return userRepository.save(user);
    }

    // PUT
    // Edit user info
    @Transactional
    public User updateUser(UserUpdateInfoRequest reqUser) {
        User dbUser = this.findById(reqUser.getId()).orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_EXISTED));

        if (reqUser.getFirstName() != null && !reqUser.getFirstName().isEmpty()
                && !reqUser.getFirstName().equals(dbUser.getFirstName())) {
            dbUser.setFirstName(reqUser.getFirstName());
        }

        if (reqUser.getLastName() != null && !reqUser.getLastName().isEmpty()
                && !reqUser.getLastName().equals(dbUser.getLastName())) {
            dbUser.setLastName(reqUser.getLastName());
        }

        if (reqUser.getGender() != null && !reqUser.getGender().equals(dbUser.getGender().toString())) {
            dbUser.setGender(reqUser.getGender().equals("Male") ? Gender.Male
                : reqUser.getGender().equals("Female") ? Gender.Female
                : Gender.Other);
        }

        if (reqUser.getBio() != null && !reqUser.getBio().isEmpty()
                && !reqUser.getBio().equals(dbUser.getBio())) {
            dbUser.setBio(reqUser.getBio());
        }
        
        if (reqUser.getJob() != null && !reqUser.getJob().isEmpty()
                && !reqUser.getJob().equals(dbUser.getJob())) {
            dbUser.setJob(reqUser.getJob());
        }

        if (reqUser.getDob() != null) {
            LocalDate parsedDate = OffsetDateTime.parse(reqUser.getDob()).toLocalDate();
            if (!parsedDate.equals(dbUser.getDob())) {
                dbUser.setDob(parsedDate);
            }
        }

        if (reqUser.getLocation() != null && !reqUser.getLocation().isEmpty()
                && !reqUser.getLocation().equals(dbUser.getLocation())) {
            dbUser.setLocation(reqUser.getLocation());
        }

        return this.userRepository.save(dbUser);
    }

    // Update user avatar and cover photo
    @Transactional
    public User updateUserImages(UserUpdateImageRequest reqUser) {
        User dbUser = this.findById(reqUser.getId()).orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_EXISTED));
        
        if (reqUser.getAvatarPublicId() != null && !reqUser.getAvatarPublicId().isEmpty()
                && !reqUser.getAvatarPublicId().equals(dbUser.getAvatarPublicId())) {
            dbUser.setAvatarPublicId(reqUser.getAvatarPublicId());
        }

        if (reqUser.getAvatarUrl() != null && !reqUser.getAvatarUrl().isEmpty()
                && !reqUser.getAvatarUrl().equals(dbUser.getAvatarUrl())) {
            dbUser.setAvatarUrl(reqUser.getAvatarUrl());
        }
        
        if (reqUser.getCoverPhotoPublicId() != null && !reqUser.getCoverPhotoPublicId().isEmpty()
                && !reqUser.getCoverPhotoPublicId().equals(dbUser.getCoverPhotoPublicId())) {
            dbUser.setCoverPhotoPublicId(reqUser.getCoverPhotoPublicId());
        }

        if (reqUser.getCoverPhotoUrl() != null && !reqUser.getCoverPhotoUrl().isEmpty()
                && !reqUser.getCoverPhotoUrl().equals(dbUser.getCoverPhotoUrl())) {
            dbUser.setCoverPhotoUrl(reqUser.getCoverPhotoUrl());
        }

        return this.userRepository.save(dbUser);
    }
    
    // Update user avatar and cover photo
    @Transactional
    public User updateUserLinks(UserUpdateLinksRequest reqUser) {
        User dbUser = this.findById(reqUser.getId()).orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_EXISTED));
        
        if (reqUser.getFacebook() != null && !reqUser.getFacebook().isEmpty()
                && !reqUser.getFacebook().equals(dbUser.getFacebook())) {
            dbUser.setFacebook(reqUser.getFacebook());
        }

        if (reqUser.getTwitter() != null && !reqUser.getTwitter().isEmpty()
                && !reqUser.getTwitter().equals(dbUser.getTwitter())) {
            dbUser.setTwitter(reqUser.getTwitter());
        }
        
        if (reqUser.getInstagram() != null && !reqUser.getInstagram().isEmpty()
                && !reqUser.getInstagram().equals(dbUser.getInstagram())) {
            dbUser.setInstagram(reqUser.getInstagram());
        }

        if (reqUser.getLinkedin() != null && !reqUser.getLinkedin().isEmpty()
                && !reqUser.getLinkedin().equals(dbUser.getLinkedin())) {
            dbUser.setLinkedin(reqUser.getLinkedin());
        }
        
        if (reqUser.getYoutube() != null && !reqUser.getYoutube().isEmpty()
                && !reqUser.getYoutube().equals(dbUser.getYoutube())) {
            dbUser.setYoutube(reqUser.getYoutube());
        }

        if (reqUser.getGithub() != null && !reqUser.getGithub().isEmpty()
                && !reqUser.getGithub().equals(dbUser.getGithub())) {
            dbUser.setGithub(reqUser.getGithub());
        }
        
        if (reqUser.getTiktok() != null && !reqUser.getTiktok().isEmpty()
                && !reqUser.getTiktok().equals(dbUser.getTiktok())) {
            dbUser.setTiktok(reqUser.getTiktok());
        }

        if (reqUser.getDiscord() != null && !reqUser.getDiscord().isEmpty()
                && !reqUser.getDiscord().equals(dbUser.getDiscord())) {
            dbUser.setDiscord(reqUser.getDiscord());
        }

        return this.userRepository.save(dbUser);
    }

    @Transactional
    public User updateUserOtp(UserUpdateOtpRequest reqUser) {
        User dbUser = this.findById(reqUser.getId()).orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_EXISTED));

        if (reqUser.getOtp() != null && !reqUser.getOtp().isEmpty()
                && !reqUser.getOtp().equals(dbUser.getOtp())) {
            dbUser.setOtp(reqUser.getOtp());
        }

        if (reqUser.getOtpGeneratedTime() != null && !reqUser.getOtpGeneratedTime().equals(dbUser.getOtpGeneratedTime())) {
            dbUser.setOtpGeneratedTime(reqUser.getOtpGeneratedTime());
        }

        if (reqUser.isActive() != dbUser.isActive()) {
            dbUser.setActive(reqUser.isActive());
        }
        
        return this.userRepository.save(dbUser);
    }

    // DELETE
    public void deleteUserById(String id) {
        User dbUser = this.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_EXISTED));

        userRepository.delete(dbUser);
    }

    // Other methods

    // Map to DTO with mutual friends count for many users
    private Page<UserDTO> getUsersWithMutualFriendsCount(String userId, Page<User> users) {
        List<String> userIds = users.getContent().stream().map(User::getId).collect(Collectors.toList());
        List<Object[]> results = userRepository.countMutualFriendsForUsers(userId, userIds);

        Map<String, Long> mutualFriendsCount = results.stream()
            .collect(Collectors.toMap(
                row -> (String) row[0],
                row -> (Long) row[1]
            ));

        return users.map(user -> {
            UserDTO dto = userMapper.toUserDTO(user);
            dto.setMutualFriendsNum(mutualFriendsCount.getOrDefault(user.getId(), 0L));

            Friendship friend = friendshipRepository.findBy2UserIds(userId, user.getId());

            if(friend == null) {
                dto.setFriend(false);
                dto.setFriendRequestSent(false);
            } else if(friend.getStatus() == FriendshipStatus.Pending && Objects.equals(friend.getSender().getId(), user.getId())) {
                dto.setFriend(false);
                dto.setFriendRequestSent(true);
            } else if(friend.getStatus() == FriendshipStatus.Accepted) {
                dto.setFriend(true);
                dto.setFriendRequestSent(false);
                
                String conversationId = conversationService.getDirectMessageId(userId, user.getId());
                dto.setConversationId(conversationId);
            }

            return dto;
        });
    }

    public void updateUserToken(String token, String emailUsernamePhone) {
        User currentUser = this.handleGetUserByUsernameOrEmailOrPhone(emailUsernamePhone);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public User getUserByRefreshTokenAndEmailOrUsernameOrPhone(String token, String emailUsernamePhone) {
        return this.userRepository.findByRefreshTokenAndEmailOrUsernameOrPhone(token, emailUsernamePhone)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_EXISTED));
    }

    // Get User by username/email/phone
    public User handleGetUserByUsernameOrEmailOrPhone(String loginInput) {
        Optional<User> optionalUser = this.userRepository.findByUsername(loginInput);
        log.info("login input: {}", loginInput);
        if (optionalUser.isEmpty()) {
            optionalUser = userRepository.findByEmailAndIsActiveTrue(loginInput);
        }
        if (optionalUser.isEmpty()) {
            optionalUser = userRepository.findByPhoneAndIsActiveTrue(loginInput);
        }
        if (optionalUser.isEmpty()) {
            throw new AppException(ErrorCode.ENTITY_NOT_EXISTED);
        }
        return optionalUser.get();
    }

    public User handleGetUserByLoginInput(String loginInput) {
        return userRepository.findByEmailOrPhone(loginInput)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_EXISTED));
    }    

    public User getUserByEmail(String email) {
        log.info("#register - email " + email);
        return this.userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_EXISTED));
    }

    public boolean verifyOtp(String userId, String otp) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_EXISTED));
        if (user.getOtp().equals(otp) && Duration.between(user.getOtpGeneratedTime(), Instant.now()).getSeconds() < 300) {
            user.setOtp(otp);
            userRepository.save(user);
            return true;
        } else if (!user.getOtp().equals(otp)) {
            throw new AppException(ErrorCode.INVALID_OTP);
        } else {
            throw new AppException(ErrorCode.EXPIRED_OTP);
        }
    }
}
