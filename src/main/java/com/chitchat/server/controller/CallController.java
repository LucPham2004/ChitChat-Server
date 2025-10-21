package com.chitchat.server.controller;

import com.chitchat.server.dto.SignalMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE, makeFinal=true)
public class CallController {
    SimpMessagingTemplate simpMessagingTemplate;


    // A gửi request gọi B
    @MessageMapping("/call/request")
    public void callRequest(SignalMessage req) {
        // chuyển tiếp tới topic riêng của người nhận
        simpMessagingTemplate.convertAndSend("/topic/private-" + req.to, req);
    }

    // Offer
    @MessageMapping("/call/offer")
    public void offer(SignalMessage offer) {
        simpMessagingTemplate.convertAndSend("/topic/private-" + offer.to, offer);
    }

    // Answer
    @MessageMapping("/call/answer")
    public void answer(SignalMessage answer) {
        simpMessagingTemplate.convertAndSend("/topic/private-" + answer.to, answer);
    }

    // ICE candidate
    @MessageMapping("/call/ice")
    public void ice(SignalMessage c) {
        simpMessagingTemplate.convertAndSend("/topic/private-" + c.to, c);
    }
}
