package com.chitchat.server.service;

import com.chitchat.server.dto.request.ConversationRequest;
import com.chitchat.server.dto.response.ChatParticipants;
import com.chitchat.server.entity.Conversation;
import org.springframework.data.domain.Page;

import java.util.*;

public interface ConversationService {
    Conversation getById(Long id);

    // Get direct message conversation between two users, if not exists, create new one
    Conversation getDirectMessage(Long selfId, Long otherId);

    Page<Conversation> getByParticipantId(Long userId, int pageNum);

    Page<Conversation> getByOwnerId(Long userId, int pageNum);

    // Search conversations by name or participant names
    List<Conversation> searchConversations(String keyword, Long userId, int pageNum);


    List<ChatParticipants> getParticipantsByConvId(Long conversationId);

    Long getDirectMessageId(Long selfId, Long otherId);

    // POST METHODS

    Conversation createConversation(ConversationRequest conversationRequest);

    List<Conversation> createManyConversations(List<ConversationRequest> conversationRequests);

    // PUT METHODS

    Conversation updateConversation(ConversationRequest conversationRequest);

    Conversation updateConversationPartially(Long id, Map<String, Object> updates);

    // DELETE METHODS

    void deleteConversation(Conversation conversation);

    void deleteConversationById(Long id);

    // OTHER METHODS
    boolean existsById(Long id);

    int countByOwnerId(Long userId);
}
