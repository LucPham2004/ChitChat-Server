package com.chitchat.server.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level=AccessLevel.PRIVATE)
public class UserMessageDTO {
    String userId;
    
    Set<String> conversationIds;
    Set<String> messageIds;
    Set<Long> messageReactionIds;
}
