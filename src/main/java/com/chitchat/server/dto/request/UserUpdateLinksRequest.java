package com.chitchat.server.dto.request;

import lombok.Data;

@Data
public class UserUpdateLinksRequest {
    private String id;
    
    private String facebook;
    private String twitter;
    private String instagram;
    private String linkedin;
    private String youtube;
    private String github;
    private String tiktok;
    private String discord;
}
