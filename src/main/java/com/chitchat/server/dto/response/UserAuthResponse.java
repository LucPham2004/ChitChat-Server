package com.chitchat.server.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level=AccessLevel.PRIVATE)
public class UserAuthResponse {
     Long id;

     String username;
     String email;
     String password;

     Set<String> authorities;

     String phone;
     String avatarUrl;
     String firstName;
     String lastName;
     String location;
     String bio;
}
