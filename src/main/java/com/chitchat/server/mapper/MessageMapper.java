package com.chitchat.server.mapper;

import com.chitchat.server.dto.request.ChatRequest;
import com.chitchat.server.dto.response.ChatResponse;
import com.chitchat.server.entity.ChatMessage;
import com.chitchat.server.entity.Media;
import com.chitchat.server.entity.Message;
import com.chitchat.server.enums.MessageStatus;
import com.chitchat.server.exception.AppException;
import com.chitchat.server.exception.ErrorCode;
import com.chitchat.server.repository.ConversationRepository;
import com.chitchat.server.repository.MessageReactionRepository;
import com.chitchat.server.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal = true)
public class MessageMapper {
    ConversationRepository conversationRepository;
    MessageReactionRepository messageReactionRepository;
    UserRepository userRepository;
    
    public Message toMessage(ChatRequest request) {
        Message message = new Message();
        message.setContent(request.getContent());

        message.setSenderId(userRepository.findByIdAndIsActiveTrue(request.getSenderId()).orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_EXISTED)).getId());
        message.setReceiverIds(request.getRecipientId());
        message.setConversation(conversationRepository.findById(request.getConversationId()).orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_EXISTED)));
        message.setReactions(new HashSet<>());
        message.setTags(new HashSet<>());
        
        message.setRead(false);
        message.setStatus(MessageStatus.DELIVERED);

        return message;
    }

    public Message toMessage(ChatMessage request) {
        Message message = new Message();
        message.setContent(request.getContent());
        message.setUrl(request.getUrl());
        message.setConversation(conversationRepository.findById(request.getConversationId()).orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_EXISTED)));
        message.setSenderId(request.getSenderId());
        message.setReceiverIds(request.getReceiverId());
        message.setStatus(MessageStatus.DELIVERED);
        message.setRead(false);

        return message;
    }

    public ChatResponse toResponse(Message message) {
        ChatResponse response = new ChatResponse();
        response.setId(message.getId());
        response.setContent(message.getContent());
        response.setConversationId(message.getConversation().getId());
        response.setSenderId(message.getSenderId());
        response.setRecipientId(message.getReceiverIds());
        response.setIsRead(false);
        if(message.getReplyTo() != null) {
            Message reply = message.getReplyTo();

            ChatResponse replyResponse = new ChatResponse();
            replyResponse.setId(reply.getId());
            replyResponse.setContent(reply.getContent());
            replyResponse.setSenderId(reply.getSenderId());
            replyResponse.setConversationId(reply.getConversation().getId());
            replyResponse.setSenderId(reply.getSenderId());

            Set<Media> medias = reply.getMedias();
            if (medias != null && !medias.isEmpty()) {
                String[] publicIds = medias.stream()
                        .map(Media::getPublicId)
                        .toArray(String[]::new);

                String[] urls = medias.stream()
                        .map(Media::getUrl)
                        .toArray(String[]::new);

                String[] fileNames = medias.stream()
                        .map(Media::getFileName)
                        .toArray(String[]::new);

                Long[] heights = medias.stream()
                        .map(Media::getHeight)
                        .toArray(Long[]::new);

                Long[] widths = medias.stream()
                        .map(Media::getWidth)
                        .toArray(Long[]::new);

                String[] resourceTypes = medias.stream()
                        .map(Media::getResourceType)
                        .toArray(String[]::new);

                replyResponse.setPublicIds(publicIds);
                replyResponse.setUrls(urls);
                replyResponse.setFileNames(fileNames);
                replyResponse.setHeights(heights);
                replyResponse.setWidths(widths);
                replyResponse.setResourceTypes(resourceTypes);
            }
            replyResponse.setType(reply.getMessageType());
            replyResponse.setCallType(reply.getCallType());
            replyResponse.setCallStatus(reply.getCallStatus());
            replyResponse.setCallDuration(reply.getCallDuration());
            replyResponse.setCreatedAt(reply.getCreatedAt());
            replyResponse.setUpdatedAt(reply.getUpdatedAt());

            response.setReplyTo(replyResponse);
            response.setReplyToId(reply.getId());
        }
        response.setType(message.getMessageType());
        response.setCallType(message.getCallType());
        response.setCallStatus(message.getCallStatus());
        response.setCallDuration(message.getCallDuration());
        response.setCreatedAt(message.getCreatedAt());
        response.setUpdatedAt(message.getUpdatedAt());

        response.setReactions(messageReactionRepository.findByMessageId(message.getId()));

        Set<Media> medias = message.getMedias();
        if (medias != null && !medias.isEmpty()) {
            String[] publicIds = medias.stream()
                .map(Media::getPublicId)
                .toArray(String[]::new);

            String[] urls = medias.stream()
                .map(Media::getUrl)
                .toArray(String[]::new);
            
            String[] fileNames = medias.stream()
                .map(Media::getFileName)
                .toArray(String[]::new);

            Long[] heights = medias.stream()
                .map(Media::getHeight)
                .toArray(Long[]::new);
            
            Long[] widths = medias.stream()
                .map(Media::getWidth)
                .toArray(Long[]::new);
            
            String[] resourceTypes = medias.stream()
                .map(Media::getResourceType)
                .toArray(String[]::new);

            response.setPublicIds(publicIds);
            response.setUrls(urls);
            response.setFileNames(fileNames);
            response.setHeights(heights);
            response.setWidths(widths);
            response.setResourceTypes(resourceTypes);
        }

        return response;
    }
}
