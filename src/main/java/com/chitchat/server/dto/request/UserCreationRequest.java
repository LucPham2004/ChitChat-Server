package com.chitchat.server.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level=AccessLevel.PRIVATE)
public class UserCreationRequest {
    
    @Size(min = 8, message = "PASSWORD_INVALID")
    String password;

    @NotBlank(message = "Email must not blank")
    @Email(message = "Email is not valid")
    String email;

    String firstName;
    String lastName;
    String phone;
    String dob;

    String gender;

    String otp;
}
