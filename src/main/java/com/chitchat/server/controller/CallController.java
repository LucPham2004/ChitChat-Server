package com.chitchat.server.controller;

import com.chitchat.server.dto.request.CallAction;
import com.chitchat.server.dto.request.CallRequest;
import com.chitchat.server.dto.request.IceCandidateMsg;
import com.chitchat.server.dto.request.OfferAnswer;
import com.chitchat.server.entity.Conversation;
import com.chitchat.server.entity.Message;
import com.chitchat.server.enums.MessageStatus;
import com.chitchat.server.exception.AppException;
import com.chitchat.server.exception.ErrorCode;
import com.chitchat.server.mapper.MessageMapper;
import com.chitchat.server.repository.ConversationRepository;
import com.chitchat.server.repository.MessageRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level= AccessLevel.PRIVATE, makeFinal=true)
public class CallController {
    SimpMessagingTemplate messagingTemplate;

    MessageRepository messageRepository;
    MessageMapper messageMapper;
    ConversationRepository conversationRepository;

    private void forwardToUser(String to, Object message) {
        String destination = "/topic/private-" + to;
        messagingTemplate.convertAndSend(destination, message);
    }

    @MessageMapping("/call/request")
    public void handleCallRequest(CallRequest payload) {
        // Gói lại tin nhắn với type để frontend dễ xử lý
        forwardToUser(payload.getTo(), Map.of(
                "type", "CALL_REQUEST",
                "from", payload.getFrom(),
                "to", payload.getTo(),
                "fromName", payload.getFromName(),
                "callType", payload.getCallType()
        ));
        log.info("call type: " + payload.getCallType());

    }

    @MessageMapping("/call/accept")
    public void handleCallAccept(CallAction payload) {
        forwardToUser(payload.getTo(), Map.of("type", "CALL_ACCEPTED", "from", payload.getFrom(), "callType", payload.getCallType()));
    }

    @MessageMapping("/call/reject")
    public void handleCallReject(CallAction payload) {
        forwardToUser(payload.getTo(), Map.of("type", "CALL_REJECTED", "from", payload.getFrom()));

        Conversation conversation = conversationRepository.findDirectMessage(payload.getFrom(), payload.getTo()).orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_EXISTED));

        Set<String> receiverIds = Set.of(payload.getFrom(), payload.getTo());

        String content = String.format("Người dùng từ chối cuộc gọi %s",
                payload.getCallType().equals("video") ? "video" : "thoại");

        Message message = Message.builder()
                .conversation(conversation)
                .senderId(payload.getTo())
                .receiverIds(receiverIds)
                .content(content)
                .messageType("CALL")
                .callDuration(payload.getDuration())
                .callStatus(payload.getStatus())
                .callType(payload.getCallType())
                .status(MessageStatus.DELIVERED)
                .isRead(false)
                .reactions(new HashSet<>())
                .tags(new HashSet<>())
                .build();

        messageRepository.save(message);

        for(String participantId: conversation.getParticipantIds()) {
            messagingTemplate.convertAndSend("/topic/user/" + participantId, messageMapper.toResponse(message));
        }
    }

    @MessageMapping("/call/offer")
    public void handleOffer(OfferAnswer payload) {
        forwardToUser(payload.getTo(), Map.of("type", "OFFER", "from", payload.getFrom(), "sdp", payload.getSdp(), "callType", payload.getCallType()));
    }

    @MessageMapping("/call/answer")
    public void handleAnswer(OfferAnswer payload) {
        forwardToUser(payload.getTo(), Map.of("type", "ANSWER", "from", payload.getFrom(), "sdp", payload.getSdp(), "callType", payload.getCallType()));
    }

    @MessageMapping("/call/ice")
    public void handleIceCandidate(IceCandidateMsg payload) {
        forwardToUser(payload.getTo(), Map.of("type", "ICE", "from", payload.getFrom(), "candidate", payload.getCandidate()));
    }

    @MessageMapping("/call/hangup")
    public void handleHangup(CallAction payload) {
        forwardToUser(payload.getFrom(), Map.of(
                "type", "HANGUP",
                "from", payload.getFrom(),
                "to", payload.getTo(),
                "duration", payload.getDuration(),
                "status", payload.getStatus()
        ));
        forwardToUser(payload.getTo(), Map.of(
                "type", "HANGUP",
                "from", payload.getFrom(),
                "to", payload.getTo(),
                "duration", payload.getDuration(),
                "status", payload.getStatus()
        ));

        log.info("payload: " + payload);

        Conversation conversation = conversationRepository.findDirectMessage(payload.getFrom(), payload.getTo()).orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_EXISTED));

        Set<String> receiverIds = Set.of(payload.getFrom(), payload.getTo());

        String content = String.format("Cuộc gọi %s %s",
                payload.getCallType().equals("video") ? "video" : "thoại",
                payload.getStatus().equals("COMPLETED") ? "đã kết thúc" : "nhỡ");

        Message message = Message.builder()
                .conversation(conversation)
                .senderId(payload.getFrom())
                .receiverIds(receiverIds)
                .content(content)
                .messageType("CALL")
                .callDuration(payload.getDuration())
                .callStatus(payload.getStatus())
                .callType(payload.getCallType())
                .status(MessageStatus.DELIVERED)
                .isRead(false)
                .reactions(new HashSet<>())
                .tags(new HashSet<>())
                .build();

        messageRepository.save(message);

        for(String participantId: conversation.getParticipantIds()) {
            messagingTemplate.convertAndSend("/topic/user/" + participantId, messageMapper.toResponse(message));
        }
    }
}
