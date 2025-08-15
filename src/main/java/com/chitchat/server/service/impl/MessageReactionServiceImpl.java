package com.chitchat.server.service.impl;

import com.chitchat.server.entity.MessageReaction;
import com.chitchat.server.entity.User;
import com.chitchat.server.exception.AppException;
import com.chitchat.server.exception.ErrorCode;
import com.chitchat.server.repository.MessageReactionRepository;
import com.chitchat.server.repository.MessageRepository;
import com.chitchat.server.service.MessageReactionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
public class MessageReactionServiceImpl implements MessageReactionService {

    MessageReactionRepository messageReactionRepository;
    MessageRepository messageRepository;
    UserServiceImpl userService;

    // Get Message Reaction count
    public int getMessageReactionCount(Long messageId) {
        return messageReactionRepository.countByMessageId(messageId);
    }

    public List<MessageReaction> getMessageReactions(Long messageId) {
        if (!messageRepository.existsById(messageId)) {
            throw new AppException(ErrorCode.ENTITY_NOT_EXISTED);
        }

        return messageReactionRepository.findByMessageId(messageId);
    }

    // Create Message Reaction
    public MessageReaction createMessageReaction(Long userId, Long messageId, String emoji) {
        User user = userService.findById(userId).orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_EXISTED));
        if(user == null) {
            throw new AppException(ErrorCode.ENTITY_NOT_EXISTED);
        }

        MessageReaction messageReaction = messageReactionRepository.findByUserIdAndMessageId(userId, messageId);
        if (messageReaction == null) {
            messageReaction = new MessageReaction();
            messageReaction.setEmoji(emoji);
            messageReaction.setUserId(userId);
            messageReaction.setMessage(messageRepository.findById(messageId).orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_EXISTED)));
            messageReaction.setCreatedAt(LocalDateTime.now());
            return messageReactionRepository.save(messageReaction);
        } else {
            return messageReaction;
        }
    }

    // Delete Message Reaction
    public void deleteMessageReaction(Long userId, Long messageId) {
        User user = userService.findById(userId).orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_EXISTED));
        if (!messageRepository.existsById(messageId) || user == null) {
            throw new AppException(ErrorCode.ENTITY_NOT_EXISTED);
        }

        MessageReaction MessageReaction = messageReactionRepository.findByUserIdAndMessageId(userId, messageId);
        if (MessageReaction != null) {
            messageReactionRepository.delete(MessageReaction);
        } else {
            throw new AppException(ErrorCode.ENTITY_NOT_EXISTED);
        }
    }

}
