package com.chitchat.server.controller;

import com.chitchat.server.dto.request.ConversationRequest;
import com.chitchat.server.dto.response.ApiResponse;
import com.chitchat.server.dto.response.ChatParticipants;
import com.chitchat.server.dto.response.ConversationResponse;
import com.chitchat.server.dto.response.ConversationShortResponse;
import com.chitchat.server.entity.Conversation;
import com.chitchat.server.mapper.ConversationMapper;
import com.chitchat.server.service.ConversationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/conversations")
@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
public class ConversationController {
    
    ConversationService conversationService;
    ConversationMapper conversationMapper;
    
    // GET METHODS

    // not finished
    @GetMapping("/get/joined")
    public ApiResponse<Page<ConversationShortResponse>> getConversationsByParticipantId(
                @RequestParam String userId,
                @RequestParam int pageNum) {
        Page<Conversation> conversations = conversationService.getByParticipantId(userId, pageNum);
        return ApiResponse.<Page<ConversationShortResponse>>builder()
            .code(1000)
            .message("Get conversations by participant with id: " + userId + " successfully")
            .result(conversations.map(conv -> conversationMapper.toConversationShortResponse(conv, userId)))
            .build();
    }

    @GetMapping("/get/owned")
    public ApiResponse<Page<ConversationShortResponse>> getConversationsByOwnerId(
                @RequestParam String userId,
                @RequestParam int pageNum) {
        Page<Conversation> conversations = conversationService.getByOwnerId(userId, pageNum);
        return ApiResponse.<Page<ConversationShortResponse>>builder()
            .code(1000)
            .message("Get conversations by owner with id: " + userId + " successfully")
            .result(conversations.map(conv -> conversationMapper.toConversationShortResponse(conv, userId)))
            .build();
    }
    
    @GetMapping("/search")
    public ApiResponse<List<ConversationShortResponse>> searchConversations(
                @RequestParam String keyword,            
                @RequestParam String userId,
                @RequestParam int pageNum) {
        List<Conversation> conversations = conversationService.searchConversations(keyword,userId, pageNum);
        return ApiResponse.<List<ConversationShortResponse>>builder()
            .code(1000)
            .message("Get conversations by participant with id: " + userId + " successfully")
            .result(conversations.stream()
                .map(conv -> conversationMapper.toConversationShortResponse(conv, userId))
                .toList())
            .build();
    }
    
    @GetMapping("/get/direct-message/{selfId}/{otherId}")
    public ApiResponse<ConversationResponse> getDirectMessage(@PathVariable String selfId, @PathVariable String otherId) {
        Conversation conversation = conversationService.getDirectMessage(selfId, otherId);
        return ApiResponse.<ConversationResponse>builder()
            .code(1000)
            .message("Get conversation successfully")
            .result(conversationMapper.toConversationResponse(conversation, selfId))
            .build();
    }

    @GetMapping("/get/{convId}/{userId}")
    public ApiResponse<ConversationResponse> getConversationById(@PathVariable String convId, @PathVariable String userId) {
        Conversation conversation = conversationService.getById(convId);
        return ApiResponse.<ConversationResponse>builder()
            .code(1000)
            .message("Get conversation by id: " + convId + " successfully")
            .result(conversationMapper.toConversationResponse(conversation, userId))
            .build();
    }

    @GetMapping("/get/participants/{convId}")
    public ApiResponse<List<ChatParticipants>> getParticipantsByConvId(@PathVariable String convId) {
        List<ChatParticipants> participants = conversationService.getParticipantsByConvId(convId);
        return ApiResponse.<List<ChatParticipants>>builder()
            .code(1000)
            .message("Get conversation participants with id: " + convId + " successfully")
            .result(participants)
            .build();
    }

    // POST METHODS

    @PostMapping("/create")
    public ApiResponse<ConversationResponse> createConversation(@RequestBody ConversationRequest conversationRequest) {
        Conversation newConversation = conversationService.createConversation(conversationRequest);
        return ApiResponse.<ConversationResponse>builder()
            .code(1000)
            .message("Create conversation successfully")
            .result(conversationMapper.toConversationResponse(newConversation, conversationRequest.getOwnerId()))
            .build();
    }
    
    @PostMapping("/create/many")
    public ApiResponse<List<ConversationResponse>> createConversations(@RequestBody List<ConversationRequest> conversationRequests) {
        List<Conversation> newConversations = conversationService.createManyConversations(conversationRequests);

        return ApiResponse.<List<ConversationResponse>>builder()
            .code(1000)
            .message("Create conversations successfully")
            .result(newConversations.stream()
                .map(conversation -> conversationMapper.toConversationResponse(conversation, conversationRequests.get(0).getOwnerId()))
                .toList())
            .build();
    }

    
    // PUT METHODS

    @PutMapping("/update/{userId}")
    public ApiResponse<ConversationResponse> updateConversation(
                @RequestBody ConversationRequest conversationRequest,
                @PathVariable String userId) {
        Conversation updatedConversation = conversationService.updateConversation(conversationRequest);
        return ApiResponse.<ConversationResponse>builder()
            .code(1000)
            .message("Update conversation successfully")
            .result(conversationMapper.toConversationResponse(updatedConversation, userId))
            .build();
    }

    @PatchMapping("/update/partially/{id}/{userId}")
    public ApiResponse<ConversationResponse> updateConversationPartially(
                @PathVariable String id,
                @PathVariable String userId,
                @RequestBody Map<String, Object> updates) {
        Conversation updatedConversation = conversationService.updateConversationPartially(id, updates);
        return ApiResponse.<ConversationResponse>builder()
            .code(1000)
            .message("Update conversation successfully")
            .result(conversationMapper.toConversationResponse(updatedConversation, userId))
            .build();
    }

    // DELETE METHODS

    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> deleteConversation(@PathVariable String id) {
        Conversation conversation = conversationService.getById(id);
        conversationService.deleteConversation(conversation);
        return ApiResponse.<Void>builder()
            .code(1000)
            .message("Delete conversation with ID " + id + " successfully!")
            .build();
    }

    @GetMapping("/get/id")
    public ApiResponse<String> getDirectMessageId(@RequestParam String selfId, @RequestParam String otherId) {
        return ApiResponse.<String>builder()
            .code(1000)
            .message("Get conversation successfully")
            .result(selfId != otherId ? conversationService.getDirectMessageId(selfId, otherId) : null)
            .build();
    }

}
