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
public class ConversationShortResponse {
    
    Long id;
    String name;
    List<String> avatarUrls;
    String avatarPublicId;

    ChatResponse lastMessage;

    Long ownerId;
    Set<Long> participantIds;
    boolean isGroup;
    boolean isRead;
    
}
