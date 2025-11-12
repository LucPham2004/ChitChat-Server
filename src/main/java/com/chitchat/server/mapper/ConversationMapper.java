package com.chitchat.server.mapper;

import com.chitchat.server.dto.request.ConversationRequest;
import com.chitchat.server.dto.response.ConversationResponse;
import com.chitchat.server.dto.response.ConversationShortResponse;
import com.chitchat.server.entity.Conversation;
import com.chitchat.server.entity.Message;
import com.chitchat.server.entity.User;
import com.chitchat.server.exception.AppException;
import com.chitchat.server.exception.ErrorCode;
import com.chitchat.server.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal = true)
public class ConversationMapper {

    //MessageRepository messageRepository;
    MessageMapper messageMapper;
    UserRepository userRepository;
    
    public Conversation toConversation(ConversationRequest conversationRequest) {
        return Conversation.builder()
            .name(conversationRequest.getName() != null ?
                conversationRequest.getName() : null)
            .description(conversationRequest.getDescription())
            .avatarUrl(conversationRequest.getAvatarUrl() != null && !conversationRequest.isGroup() ? 
                conversationRequest.getAvatarUrl() : null)
            .avatarPublicId(conversationRequest.getAvatarPublicId())
            .color(conversationRequest.getColor())
            .emoji(conversationRequest.getEmoji())
            .participantIds(conversationRequest.getParticipantIds())
            .ownerId(conversationRequest.getOwnerId())
            .isGroup(conversationRequest.getParticipantIds().size() > 2)
            .isRead(conversationRequest.isRead())
            .isMuted(conversationRequest.isMuted())
            .isPinned(conversationRequest.isPinned())
            .isArchived(conversationRequest.isArchived())
            .isDeleted(conversationRequest.isDeleted())
            .isBlocked(conversationRequest.isBlocked())
            .isReported(conversationRequest.isReported())
            .isSpam(conversationRequest.isSpam())
            .isMarkedAsUnread(conversationRequest.isMarkedAsUnread())
            .isMarkedAsRead(conversationRequest.isMarkedAsRead())
            .build();
    }

    public ConversationShortResponse toConversationShortResponse(Conversation conversation, String userId) {

        // Đặt tên conversation linh hoạt từng góc nhìn
        // Danh sách participant ngoại trừ userId
        List<String> otherParticipants = conversation.getParticipantIds().stream()
        .filter(id -> !id.equals(userId))
        .toList();

        String conversationName;
        if(conversation.getName() != null) {
            conversationName = conversation.getName();
        } else if (conversation.isGroup()) {
            // Nếu là group chat -> lấy tên tất cả participants khác userId
            List<String> participantNames = otherParticipants.stream().limit(3)
                .map(id -> { 
                    User user = userRepository.findByIdAndIsActiveTrue(id).orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_EXISTED));
                    String name;
                    if(user.getLastName() == null) {
                        name = user.getFirstName();
                    } else {
                        name = user.getFirstName() + " " + user.getLastName();
                    }
                    return name;
                })
                .collect(Collectors.toList());
            conversationName = String.join(", ", participantNames);
        } else {
            // Nếu không phải group chat -> chỉ lấy tên của 1 người còn lại
            User user = userRepository.findByIdAndIsActiveTrue(otherParticipants.get(0)).orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_EXISTED));
            conversationName = user.getFirstName() + " " + user.getLastName();
        }

        // Tin nhắn cuối
        Set<Message> messages = conversation.getMessages();
        Message lastMessage = null;
        if (messages != null && !messages.isEmpty()) {
            lastMessage = messages.stream()
                .max(Comparator.comparing(Message::getCreatedAt))
                .orElse(null);
        }

        // Lấy danh sách avatarUrls
        String defaultAvatar = "/images/user_default.avif";
        Set<String> participants = conversation.getParticipantIds();
        List<String> avatarUrls = new ArrayList<>();

        if (conversation.getAvatarUrl() != null) {
            avatarUrls.add(conversation.getAvatarUrl());
        } else {
            if (conversation.isGroup()) {
                avatarUrls = participants.stream()
                    .limit(4)
                    .map(id -> {
                        User user = userRepository.findByIdAndIsActiveTrue(id).orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_EXISTED));
                        return user.getAvatarUrl() != null ? user.getAvatarUrl() : defaultAvatar;
                    })
                    .collect(Collectors.toList());
            } else {
                if (!otherParticipants.isEmpty()) {
                    User user = userRepository.findByIdAndIsActiveTrue(otherParticipants.get(0)).orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_EXISTED));
                    avatarUrls.add(user.getAvatarUrl() != null ? user.getAvatarUrl() : defaultAvatar);
                } else {
                    avatarUrls.add(defaultAvatar);
                }
            }
        }

        return ConversationShortResponse.builder()
            .id(conversation.getId())
            .name(conversationName)
            .lastMessage(lastMessage != null ? messageMapper.toResponse(lastMessage) : null)
            .avatarUrls(avatarUrls)
            .avatarPublicId(conversation.getAvatarPublicId())
            .ownerId(conversation.getOwnerId())
            .participantIds(conversation.getParticipantIds())
            .isGroup(conversation.isGroup())
            .isRead(conversation.isRead())
            .build();
    }

    public ConversationResponse toConversationResponse(Conversation conversation, String userId) {

        // Đặt tên conversation linh hoạt từng góc nhìn
        // Danh sách participant ngoại trừ userId
        List<String> otherParticipants = conversation.getParticipantIds().stream()
            .filter(id -> !id.equals(userId))
            .toList();

        String conversationName;
        if(conversation.getName() != null) {
            conversationName = conversation.getName();
        } else if (conversation.isGroup()) {
            // Nếu là group chat -> lấy tên tất cả participants khác userId
            List<String> participantNames = otherParticipants.stream().limit(3)
                .map(id -> {
                    User user = userRepository.findByIdAndIsActiveTrue(id).orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_EXISTED));
                    String name;
                    if(user.getLastName() == null) {
                        name = user.getFirstName();
                    } else {
                        name = user.getFirstName() + " " + user.getLastName();
                    }
                    return name;
                })
                .collect(Collectors.toList());
            conversationName = String.join(", ", participantNames);
        } else {
            // Nếu không phải group chat -> chỉ lấy tên của 1 người còn lại
            User user = userRepository.findByIdAndIsActiveTrue(otherParticipants.get(0)).orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_EXISTED));
            conversationName = user.getFirstName() + " " + user.getLastName();
        }

        // Tin nhắn cuối conversation
        Set<Message> messages = conversation.getMessages();
        Message lastMessage = null;
        if (messages != null && !messages.isEmpty()) {
            lastMessage = messages.stream()
                .max(Comparator.comparing(Message::getCreatedAt))
                .orElse(null);
        }

        // Lấy danh sách avatarUrls
        String defaultAvatar = "/images/user_default.avif";
        Set<String> participants = conversation.getParticipantIds();
        List<String> avatarUrls = new ArrayList<>();

        if (conversation.getAvatarUrl() != null) {
            avatarUrls.add(conversation.getAvatarUrl());
        } else {
            if (conversation.isGroup()) {
                avatarUrls = participants.stream()
                    .limit(4)
                    .map(id -> {
                        User user = userRepository.findByIdAndIsActiveTrue(id).orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_EXISTED));
                        return user.getAvatarUrl() != null ? user.getAvatarUrl() : defaultAvatar;
                    })
                    .collect(Collectors.toList());
            } else {
                if (!otherParticipants.isEmpty()) {
                    User user = userRepository.findByIdAndIsActiveTrue(otherParticipants.get(0)).orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_EXISTED));
                    avatarUrls.add(user.getAvatarUrl() != null ? user.getAvatarUrl() : defaultAvatar);
                } else {
                    avatarUrls.add(defaultAvatar);
                }
            }
        }

        return ConversationResponse.builder()
            .id(conversation.getId())
            .name(conversationName)
            .description(conversation.getDescription())
            .avatarUrls(avatarUrls)
            .avatarPublicId(conversation.getAvatarPublicId())
            .color(conversation.getColor())
            .emoji(conversation.getEmoji())
            .lastMessage(lastMessage != null ? messageMapper.toResponse(lastMessage) : null)
            .ownerId(conversation.getOwnerId())
            .participantIds(conversation.getParticipantIds())
            .isGroup(conversation.isGroup())
            .isRead(conversation.isRead())
            .isMuted(conversation.isMuted())
            .isPinned(conversation.isPinned())
            .isArchived(conversation.isArchived())
            .isDeleted(conversation.isDeleted())
            .isBlocked(conversation.isBlocked())
            .blockerId(conversation.getBlockerId())
            .isReported(conversation.isReported())
            .isSpam(conversation.isSpam())
            .isMarkedAsUnread(conversation.isMarkedAsUnread())
            .isMarkedAsRead(conversation.isMarkedAsRead())
            .build();
    }
}
