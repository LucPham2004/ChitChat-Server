package com.chitchat.server.dto.response;

import com.chitchat.server.entity.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level=AccessLevel.PRIVATE)
public class UserAuthResponse {
     String id;

     String username;
     String email;
     String password;

     Set<Role> authorities;

     String phone;
     String avatarUrl;
     String fullName;
     String firstName;
     String lastName;
     String location;
     String bio;
}
