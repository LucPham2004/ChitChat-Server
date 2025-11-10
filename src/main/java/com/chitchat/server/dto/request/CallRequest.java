package com.chitchat.server.dto.request;

import lombok.Data;

@Data
public class CallRequest {
    private String from;
    private String fromName;
    private String to;
    private String toName;
    private String callType;
}
