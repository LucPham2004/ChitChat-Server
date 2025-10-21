package com.chitchat.server.dto;

import lombok.Data;

@Data
public class CallMessage {
    private String type; // "offer", "answer", "ice-candidate", "end"
    private String sdp; // SDP data
    private String candidate; // ICE candidate
    private String roomId;
    // Getters/setters
}