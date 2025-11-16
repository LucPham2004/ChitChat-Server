package com.chitchat.server.controller;

import com.chitchat.server.dto.UserTypingStatus;
import com.chitchat.server.entity.Conversation;
import com.chitchat.server.exception.AppException;
import com.chitchat.server.exception.ErrorCode;
import com.chitchat.server.service.impl.ConversationServiceImpl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.Instant;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level= AccessLevel.PRIVATE, makeFinal=true)
public class UserStatusController {
    SimpMessagingTemplate template;
    ConversationServiceImpl conversationService;

    @MessageMapping("/user/typing")
    public void handleTypingStatus(UserTypingStatus typingStatus) {
        System.out.println("🔥 Backend received typing status: typing=" + typingStatus.getTyping()
                + ", userId=" + typingStatus.getUserId());

        try {
            Conversation conversation = conversationService
                    .getById(typingStatus.getConversationId());

            if (conversation == null) {
                throw new AppException(ErrorCode.ENTITY_NOT_EXISTED);
            }

            // Send typing status to all participants except sender
            for (String participantId : conversation.getParticipantIds()) {
                if (!participantId.equals(typingStatus.getUserId())) {
                    System.out.println("🔥 Backend sending to " + participantId
                            + ": typing=" + typingStatus.getTyping());
                    template.convertAndSend(
                            "/topic/user/" + participantId + "/typing",
                            typingStatus
                    );
                }
            }
        } catch (Exception e) {
            log.error("Error handling typing status", e);
        }
    }

}
