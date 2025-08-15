package com.chitchat.server.controller;

import com.chitchat.server.dto.response.ApiResponse;
import com.chitchat.server.entity.MessageReaction;
import com.chitchat.server.service.MessageReactionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message-reactions")
@RequiredArgsConstructor
@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
public class MessageReactionController {
    MessageReactionService messageReactionService;

    // Get messageReaction
    @GetMapping("/get/count/message/{messageId}")
    public ApiResponse<Integer> getMessageReactionCount(@PathVariable Long messageId) {
        int messageReactionCount = messageReactionService.getMessageReactionCount(messageId);
        return ApiResponse.<Integer>builder()
            .code(1000)
            .message("get message messageReaction successfully")
            .result(messageReactionCount)
            .build();
    }

    @GetMapping("/get/all/{messageId}")
    public ApiResponse<List<MessageReaction>> getMessageReactions(@PathVariable Long messageId) {
        List<MessageReaction> messageReactions = messageReactionService.getMessageReactions(messageId);
        return ApiResponse.<List<MessageReaction>>builder()
            .code(1000)
            .message("get message reactions successfully")
            .result(messageReactions)
            .build();
    }
    
    // Create messageReaction
    @PostMapping
    public ApiResponse<MessageReaction> createMessageReaction(
            @RequestParam Long userId, 
            @RequestParam Long messageId,
            @RequestParam String emoji) {
        MessageReaction messageReaction = messageReactionService.createMessageReaction(userId, messageId, emoji);
        return ApiResponse.<MessageReaction>builder()
            .code(1000)
            .message("Create message reaction successfully")
            .result(messageReaction)
            .build();
    }

    // Delete messageReaction
    @DeleteMapping("/remove/user/{userId}/message/{messageId}")
    public ApiResponse<String> deleteMessageReaction(@PathVariable Long userId, @PathVariable Long messageId) {
        messageReactionService.deleteMessageReaction(userId, messageId);
        return ApiResponse.<String>builder()
            .code(1000)
            .message("Delete message reaction successfully")
            .result("")
            .build();
    }

}
