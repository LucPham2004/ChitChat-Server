package com.chitchat.server.dto.request;

import lombok.Data;

import java.time.Instant;

@Data
public class UserUpdateOtpRequest {
    private Long id;

    private String otp;

    private Instant otpGeneratedTime;

    private boolean isActive;
}
