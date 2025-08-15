package com.chitchat.server.service;

import com.chitchat.server.dto.request.ChatRequest;
import com.chitchat.server.entity.Message;
import org.springframework.data.domain.Page;

public interface MessageService {
    Message getMessage(Long messageId);

    Page<Message> getConversationMessages(Long conversationId, int pageNum);

    Page<Message> getUserMessages(Long senderId, int pageNum);

    // Search messages by keyword in a conversation
    Page<Message> findMessagesByKeyword(Long conversationId, String keyword, int pageNum);

    // Send Message
    void sendMessage(ChatRequest chatRequest);

    // Delete Message
    void deleteMessage(Long messageId);

    // Update Message
    Message updateMessage(Long messageId, String content);
}
