package com.chitchat.server.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

     @NotBlank(message = "Email must not blank")
     @Email(message = "Email is not valid")
     String email;

     @NotBlank
     private String password;
}
