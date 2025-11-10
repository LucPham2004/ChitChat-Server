package com.chitchat.server.dto.request;

import lombok.Data;

@Data
public class OfferAnswer {
    private String from;
    private String to;
    private String sdp;
    private String callType;
}
