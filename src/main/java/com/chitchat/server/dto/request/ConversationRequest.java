package com.chitchat.server.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level=AccessLevel.PRIVATE)
public class ConversationRequest {

    Long id;
    String name;
    String description;
    String color;
    String emoji;
    String avatarPublicId;
    String avatarUrl;

    Set<Long> participantIds;
    Long ownerId;

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
