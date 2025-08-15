package com.chitchat.server.service;

import com.chitchat.server.entity.MessageReaction;
import java.util.List;

public interface MessageReactionService {
    int getMessageReactionCount(Long messageId);

    List<MessageReaction> getMessageReactions(Long messageId);

    // Create Message Reaction
    MessageReaction createMessageReaction(Long userId, Long messageId, String emoji);

    // Delete Message Reaction
    void deleteMessageReaction(Long userId, Long messageId);
}
