package com.chitchat.server.service;

import com.chitchat.server.dto.request.ConversationRequest;
import com.chitchat.server.dto.response.ChatParticipants;
import com.chitchat.server.entity.Conversation;
import org.springframework.data.domain.Page;

import java.util.*;

public interface ConversationService {
    Conversation getById(String id);

    // Get direct message conversation between two users, if not exists, create new one
    Conversation getDirectMessage(String selfId, String otherId);

    Page<Conversation> getByParticipantId(String userId, int pageNum);

    Page<Conversation> getByOwnerId(String userId, int pageNum);

    // Search conversations by name or participant names
    List<Conversation> searchConversations(String keyword, String userId, int pageNum);


    List<ChatParticipants> getParticipantsByConvId(String conversationId);

    String getDirectMessageId(String selfId, String otherId);

    // POST METHODS

    Conversation createConversation(ConversationRequest conversationRequest);

    List<Conversation> createManyConversations(List<ConversationRequest> conversationRequests);

    // PUT METHODS

    Conversation updateConversation(ConversationRequest conversationRequest);

    Conversation updateConversationPartially(String id, Map<String, Object> updates);

    // DELETE METHODS

    void deleteConversation(Conversation conversation);

    void deleteConversationById(String id);

    // OTHER METHODS
    boolean existsById(String id);

    int countByOwnerId(String userId);
}
