package com.chitchat.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTypingStatus {
    private String userId;
    private String conversationId;
    private Boolean typing;
    private String timestamp; // ISO-8601 format
}
