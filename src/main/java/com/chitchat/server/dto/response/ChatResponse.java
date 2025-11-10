package com.chitchat.server.dto.response;

import com.chitchat.server.entity.MessageReaction;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level=AccessLevel.PRIVATE)
public class ChatResponse {
    String id;
    String conversationId;
    String senderId;
    Set<String> recipientId;
    
    String content;

    List<MessageReaction> reactions;

    String[] publicIds;
    String[] urls;
    String[] fileNames;
    Long[] heights;
    Long[] widths;
    String[] resourceTypes;

    String type;
    String callType;
    String callStatus;
    Long callDuration;

    Boolean isRead;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
