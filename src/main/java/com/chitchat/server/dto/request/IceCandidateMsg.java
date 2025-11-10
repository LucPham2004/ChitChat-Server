package com.chitchat.server.dto.request;

import lombok.Data;

@Data
public class IceCandidateMsg {
    private String from;
    private String to;
    private String candidate;
}