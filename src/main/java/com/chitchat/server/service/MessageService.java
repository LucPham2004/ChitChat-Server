package com.chitchat.server.service;

import com.chitchat.server.dto.request.ChatRequest;
import com.chitchat.server.entity.Message;
import org.springframework.data.domain.Page;

public interface MessageService {
    Message getMessage(String messageId);

    Page<Message> getConversationMessages(String conversationId, int pageNum);

    Page<Message> getUserMessages(String senderId, int pageNum);

    // Search messages by keyword in a conversation
    Page<Message> findMessagesByKeyword(String conversationId, String keyword, int pageNum);

    // Send Message
    void sendMessage(ChatRequest chatRequest);

    void handleCallRequest(ChatRequest request);

    void handleTypingStatus(ChatRequest request);

    // Delete Message
    void deleteMessage(String messageId);

    // Update Message
    Message updateMessage(String messageId, String content);
}
