package com.chitchat.server.dto.request;

import com.chitchat.server.entity.MessageReaction;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level=AccessLevel.PRIVATE)
public class ChatRequest {
    Long id;
    Long conversationId;
    Long senderId;
    Set<Long> recipientId;
    
    String content;
    
    List<MessageReaction> reactions;

    String[] publicIds;
    String[] urls;
    String[] fileNames;
    Long[] heights;
    Long[] widths;
    String[] resourceTypes;
}
