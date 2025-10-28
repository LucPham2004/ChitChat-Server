package com.chitchat.server.entity;

import com.chitchat.server.enums.MessageStatus;
import lombok.*;

import java.util.Set;

@Getter 
@Setter 
@AllArgsConstructor 
@NoArgsConstructor 
@Builder 
public class ChatMessage { 
    private MessageStatus status;
    private String content; 
    private String conversationId;
    private String senderId;
    private Set<String> receiverId;
    private String url;
    private String createdAt;
}
