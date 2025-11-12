package com.chitchat.server.dto.request;

import com.chitchat.server.dto.response.ChatResponse;
import com.chitchat.server.entity.Message;
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
public class ChatRequest {
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

    String type; // MESSAGE, CALL_REQUEST, CALL_ACCEPT, CALL_REJECT, CALL_END, TYPING_START, TYPING_STOP
    String callType; // video, audio
    String senderName; // For notifications

    String callStatus;
    Long callDuration;

    Long timestamp;
    LocalDateTime createdAt;

    String ReplyToId;
    ChatResponse ReplyTo;
}
