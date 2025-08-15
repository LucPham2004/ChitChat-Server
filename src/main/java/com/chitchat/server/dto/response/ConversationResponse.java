package com.chitchat.server.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level=AccessLevel.PRIVATE)
public class ConversationResponse {
    
    Long id;
    String name;
    String description;
    String color;
    String emoji;
    List<String> avatarUrls;
    String avatarPublicId;

    ChatResponse lastMessage;

    Long ownerId;
    Set<Long> participantIds;
    
    boolean isGroup;
    boolean isRead;
    boolean isMuted;
    boolean isPinned;
    boolean isArchived;
    boolean isDeleted;
    boolean isBlocked;
    boolean isReported;
    boolean isSpam;
    boolean isMarkedAsUnread;
    boolean isMarkedAsRead;
}
