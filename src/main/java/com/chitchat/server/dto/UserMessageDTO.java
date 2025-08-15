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
    Long userId;
    
    Set<Long> conversationIds;
    Set<Long> messageIds;
    Set<Long> messageReactionIds;
}
