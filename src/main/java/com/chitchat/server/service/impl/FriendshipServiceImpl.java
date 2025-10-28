package com.chitchat.server.service.impl;

import com.chitchat.server.entity.Conversation;
import com.chitchat.server.entity.Friendship;
import com.chitchat.server.enums.FriendshipStatus;
import com.chitchat.server.exception.AppException;
import com.chitchat.server.exception.ErrorCode;
import com.chitchat.server.repository.ConversationRepository;
import com.chitchat.server.repository.FriendshipRepository;
import com.chitchat.server.repository.UserRepository;
import com.chitchat.server.service.FriendshipService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FriendshipServiceImpl implements FriendshipService {

    FriendshipRepository friendShipRepository;
    UserServiceImpl userService;
    UserRepository userRepository;
    ConversationRepository conversationRepository;
    
    public Friendship getFriendStatus(String requesterId, String receiverId) {
        if (!userRepository.existsByIdAndIsActiveTrue(requesterId) || !userRepository.existsByIdAndIsActiveTrue(receiverId)) {
            throw new AppException(ErrorCode.ENTITY_NOT_EXISTED);
        }

        return friendShipRepository.findBy2UserIds(requesterId, receiverId);
    }

    public Friendship sendFriendRequest(String requesterId, String receiverId) {
        if (!userRepository.existsByIdAndIsActiveTrue(requesterId) || !userRepository.existsByIdAndIsActiveTrue(receiverId)) {
            throw new AppException(ErrorCode.ENTITY_NOT_EXISTED);
        }
        Friendship friendship = friendShipRepository.findBy2UserIds(requesterId, receiverId);
        if(friendship != null) {
            return friendship;
        }
        friendship = new Friendship();
        friendship.setSender(userService.findById(requesterId).orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_EXISTED)));
        friendship.setRecipient(userService.findById(receiverId).orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_EXISTED)));
        friendship.setStatus(FriendshipStatus.Pending);

        return friendShipRepository.save(friendship);
    }

    public void deleteFriendShip(String iselfId, String otherId) {
        Friendship friendship = friendShipRepository.findBy2UserIds(iselfId, otherId);
        if (friendship == null) {
            throw new AppException(ErrorCode.ENTITY_NOT_EXISTED);
        }

        friendShipRepository.delete(friendship);
    }

    @Transactional
    public Friendship editFriendShipStatus(String selfId, String otherId, FriendshipStatus status) {
        Friendship friendship = friendShipRepository.findBy2UserIds(selfId, otherId);

        friendship.setStatus(status);

        if(status == FriendshipStatus.Blocked) {
            Optional<Conversation> optionalConversation = conversationRepository.findDirectMessage(selfId, otherId);
            if(optionalConversation.isPresent()) {
                Conversation conversation = optionalConversation.get();

                conversation.setBlockerId(selfId);
                conversation.setBlocked(true);

                conversationRepository.save(conversation);
            }
        }

        return friendShipRepository.save(friendship);
    }
}
