package com.chitchat.server.dto;

import lombok.Data;

@Data
public class SignalMessage {
    public String from;
    public String to;
    private String type;      // "offer" | "answer" | "candidate" | "end" | "ring"...
    private String senderId;
    private String[] recipientId;
    private String sdp;       // cho offer/answer
    private String candidate; // cho ICE
    private String sdpMid;
    private Integer sdpMLineIndex;
    private String callId;    // để track 1 cuộc gọi
    private String callType; // video, audio
    private String senderName;
    private Long timestamp;
}

