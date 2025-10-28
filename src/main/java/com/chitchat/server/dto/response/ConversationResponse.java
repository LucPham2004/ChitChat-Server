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

    String id;
    String name;
    String description;
    String color;
    String emoji;
    List<String> avatarUrls;
    String avatarPublicId;

    ChatResponse lastMessage;

    String ownerId;
    String blockerId;
    Set<String> participantIds;
    
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
