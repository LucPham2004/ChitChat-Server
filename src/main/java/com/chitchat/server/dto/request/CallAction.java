package com.chitchat.server.dto.request;

import lombok.Data;

@Data
public class CallAction {
    private String from;
    private String to;
    private String callType; // Thêm callType cho ACCEPT
    private Long duration;
    private String status;
}
