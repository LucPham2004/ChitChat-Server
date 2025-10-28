package com.chitchat.server.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordRequest {

    @NotBlank(message = "Email must not blank")
    @Email(message = "Email is not valid")
    private String email;
}
