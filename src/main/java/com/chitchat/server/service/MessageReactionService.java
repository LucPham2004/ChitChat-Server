package com.chitchat.server.service;

import com.chitchat.server.entity.MessageReaction;
import java.util.List;

public interface MessageReactionService {
    int getMessageReactionCount(String messageId);

    List<MessageReaction> getMessageReactions(String messageId);

    // Create Message Reaction
    MessageReaction createMessageReaction(String userId, String messageId, String emoji);

    // Delete Message Reaction
    void deleteMessageReaction(String userId, String messageId);
}
