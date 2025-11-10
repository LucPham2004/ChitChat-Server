package com.chitchat.server.controller;

import com.chitchat.server.dto.request.ChatRequest;
import com.chitchat.server.dto.response.ApiResponse;
import com.chitchat.server.dto.response.ChatResponse;
import com.chitchat.server.entity.Message;
import com.chitchat.server.entity.User;
import com.chitchat.server.exception.AppException;
import com.chitchat.server.exception.ErrorCode;
import com.chitchat.server.mapper.MessageMapper;
import com.chitchat.server.service.MessageService;
import com.chitchat.server.service.impl.UserServiceImpl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/messages")
@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
public class MessageController {

    MessageService service;
    MessageMapper chatMapper;
    UserServiceImpl userService;
    private final SimpMessagingTemplate template;

    // Get messages

    @GetMapping("/get/{id}")
    public ApiResponse<ChatResponse> getMessageById(@PathVariable String id) {
        Message message = service.getMessage(id);
        return ApiResponse.<ChatResponse>builder()
            .code(1000)
            .message("Get message with id: " + id + " successfully")
            .result(chatMapper.toResponse(message))
            .build();
    }

    @GetMapping("/get/conversation")
    public ApiResponse<Page<ChatResponse>> getConversationMessages(
                @RequestParam String conversationId,
                @RequestParam int pageNum) {
        Page<Message> messages = service.getConversationMessages(conversationId, pageNum);
        return ApiResponse.<Page<ChatResponse>>builder()
            .code(1000)
            .message("Get messages by conversation with id: " + conversationId + " successfully")
            .result(messages.map(chatMapper::toResponse))
            .build();
    }

    @GetMapping("/get/user")
    public ApiResponse<Page<ChatResponse>> getUserMessages(
                @RequestParam String senderId,
                @RequestParam int pageNum) {
        Page<Message> messages = service.getUserMessages(senderId, pageNum);
        return ApiResponse.<Page<ChatResponse>>builder()
            .code(1000)
            .message("Get messages by sender with id: " + senderId + " successfully")
            .result(messages.map(chatMapper::toResponse))
            .build();
    }

    // Search messages by keyword in a conversation
    @GetMapping("/search")
    public ApiResponse<Page<ChatResponse>> findMessagesByKeyword(
                @RequestParam String conversationId,
                @RequestParam String keyword,
                @RequestParam int pageNum) {
        Page<Message> messages = service.findMessagesByKeyword(conversationId, keyword, pageNum);
        return ApiResponse.<Page<ChatResponse>>builder()
            .code(1000)
            .message("Get messages with keyword: " + keyword + " in conversation with id: " + conversationId + " successfully")
            .result(messages.map(chatMapper::toResponse))
            .build();
    }

    // Send message
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic")
    public void sendMessage(@Payload ChatRequest request) {
        try {
            User sender = userService.findById(request.getSenderId()).orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_EXISTED));
            request.setSenderName(sender.getFirstName());
            request.setTimestamp(System.currentTimeMillis());

            // Process message based on type
            switch (request.getType()) {
                case "message":
                    service.sendMessage(request);
                    break;
                case "TYPING_START":
                case "TYPING_STOP":
                    service.handleTypingStatus(request);
                    break;
                default:
                    service.sendMessage(request); // Default to regular message
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }

    // Delete message
    @DeleteMapping("/delete/{messageId}")
    public ApiResponse<Void> deleteMessage(@PathVariable String messageId) {
        this.service.deleteMessage(messageId);
        return ApiResponse.<Void>builder()
            .code(1000)
            .message("Delete message with ID " + messageId + " successfully!")
            .build();
    }

    // Update message
    @PutMapping("/update")
    public ApiResponse<Void> updateMessage(
                @RequestParam String messageId,
                @RequestParam String content) {
        this.service.updateMessage(messageId, content);
        return ApiResponse.<Void>builder()
            .code(1000)
            .message("Update message with ID " + messageId + " successfully!")
            .build();
    }

}
