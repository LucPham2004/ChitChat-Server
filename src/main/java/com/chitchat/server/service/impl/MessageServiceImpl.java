package com.chitchat.server.service.impl;

import com.chitchat.server.dto.request.ChatRequest;
import com.chitchat.server.entity.Conversation;
import com.chitchat.server.entity.Media;
import com.chitchat.server.entity.Message;
import com.chitchat.server.entity.User;
import com.chitchat.server.exception.AppException;
import com.chitchat.server.exception.ErrorCode;
import com.chitchat.server.mapper.MessageMapper;
import com.chitchat.server.repository.ConversationRepository;
import com.chitchat.server.repository.MediaRepository;
import com.chitchat.server.repository.MessageRepository;
import com.chitchat.server.service.MessageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
public class MessageServiceImpl implements MessageService {

    MessageRepository messageRepository;
    MessageMapper messageMapper;
    ConversationRepository conversationRepository;
    ConversationServiceImpl conversationService;
    UserServiceImpl userService;
    MediaRepository mediaRepository;
    SimpMessagingTemplate template;

    static int MESSAGES_PER_PAGE = 20;

    // Get messages

    public Message getMessage(String messageId) {
        if(!messageRepository.existsById(messageId)) {
            throw new AppException(ErrorCode.ENTITY_NOT_EXISTED);
        }
        return messageRepository.findById(messageId).orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_EXISTED));
    }

    public Page<Message> getConversationMessages(String conversationId, int pageNum) {
        if(!conversationRepository.existsById(conversationId)) {
            throw new AppException(ErrorCode.ENTITY_NOT_EXISTED);
        }
        // Get nearest messages by conversation
        Pageable pageable = PageRequest.of(pageNum, MESSAGES_PER_PAGE, Sort.by(Sort.Direction.DESC, "createdAt"));

        return messageRepository.findByConversationId(conversationId, pageable);
    }

    public Page<Message> getUserMessages(String senderId, int pageNum) {
        User user = userService.findById(senderId).orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_EXISTED));
        if(user == null) {
            throw new AppException(ErrorCode.ENTITY_NOT_EXISTED);
        }
        Pageable pageable = PageRequest.of(pageNum, MESSAGES_PER_PAGE);

        return messageRepository.findBySenderId(senderId, pageable);
    }

    // Search messages by keyword in a conversation
    public Page<Message> findMessagesByKeyword(String conversationId, String keyword, int pageNum) {
        if(!conversationRepository.existsById(conversationId)) {
            throw new AppException(ErrorCode.ENTITY_NOT_EXISTED);
        }
        Pageable pageable = PageRequest.of(pageNum, MESSAGES_PER_PAGE);

        return messageRepository.findByConversationIdAndContentContainingIgnoreCase(conversationId, keyword, pageable);
    }

    // Send Message
    public void sendMessage(ChatRequest chatRequest) {
        Conversation conversation = conversationRepository.findById(chatRequest.getConversationId()).orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_EXISTED));

        Message message = messageMapper.toMessage(chatRequest);
        messageRepository.save(message);

        chatRequest.setId(message.getId());

        for(String participantId: conversation.getParticipantIds()) {
            template.convertAndSend("/topic/user/" + participantId, chatRequest);
        }

        handleMessageMedia(chatRequest, message, conversation);
    }

    // Handle call requests
    public void handleCallRequest(ChatRequest request) {
        // Save call request as a special message type
        Conversation conversation = conversationRepository.findById(request.getConversationId())
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_EXISTED));

        Message callMessage = Message.builder()
                .conversation(conversation)
                .senderId(request.getSenderId())
                .content("📞 " + (request.getCallType().equals("video") ? "Video" : "Audio") + " call")
                .messageType("CALL_REQUEST")
                .build();

        messageRepository.save(callMessage);
        request.setId(callMessage.getId());

        // Send to all participants
        for (String participantId : conversation.getParticipantIds()) {
            template.convertAndSend("/topic/user/" + participantId, request);
        }
    }

    // Handle typing status
    public void handleTypingStatus(ChatRequest request) {
        // Don't save typing status to database, just broadcast
        Conversation conversation = conversationRepository.findById(request.getConversationId())
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_EXISTED));

        // Send to other participants (not sender)
        for (String participantId : conversation.getParticipantIds()) {
            if (!participantId.equals(request.getSenderId())) {
                template.convertAndSend("/topic/user/" + participantId, request);
            }
        }
    }

    private void handleMessageMedia(ChatRequest chatRequest, Message message, Conversation conversation) {
        // Your existing media handling code
        if (chatRequest.getPublicIds() != null && chatRequest.getUrls() != null &&
                chatRequest.getHeights() != null && chatRequest.getWidths() != null &&
                chatRequest.getResourceTypes() != null && chatRequest.getFileNames() != null) {

            if (chatRequest.getPublicIds().length != chatRequest.getUrls().length
                    || chatRequest.getPublicIds().length != chatRequest.getFileNames().length
                    || chatRequest.getPublicIds().length != chatRequest.getHeights().length
                    || chatRequest.getPublicIds().length != chatRequest.getWidths().length
                    || chatRequest.getPublicIds().length != chatRequest.getResourceTypes().length) {
                throw new IllegalArgumentException("The size of publicIds and urls/heights/widths/types must be the same.");
            }

            Set<Media> medias = new HashSet<>();

            for (int i = 0; i < chatRequest.getPublicIds().length; i++) {
                Media media = new Media();
                media.setPublicId(chatRequest.getPublicIds()[i]);
                media.setUrl(chatRequest.getUrls()[i]);
                media.setFileName(chatRequest.getFileNames()[i]);
                media.setHeight(chatRequest.getHeights()[i]);
                media.setWidth(chatRequest.getWidths()[i]);
                media.setResourceType(chatRequest.getResourceTypes()[i]);

                media.setMessage(message);
                media.setConversation(conversation);

                medias.add(mediaRepository.save(media));
            }

            message.setMedias(medias);
            conversation.setMedias(medias);
            messageRepository.save(message);
            conversationRepository.save(conversation);
        }
    }

    // Delete Message
    public void deleteMessage(String messageId) {
        if(!messageRepository.existsById(messageId)) {
            throw new AppException(ErrorCode.ENTITY_NOT_EXISTED);
        }
        messageRepository.delete(messageRepository.findById(messageId).orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_EXISTED)));
    }

    // Update Message
    public Message updateMessage(String messageId, String content) {
        if(!messageRepository.existsById(messageId)) {
            throw new AppException(ErrorCode.ENTITY_NOT_EXISTED);
        }
        Message message = messageRepository.findById(messageId).orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_EXISTED));
        message.setContent(content);
        return messageRepository.save(message);
    }
}
