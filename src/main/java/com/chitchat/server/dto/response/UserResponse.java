package com.chitchat.server.dto.response;

import com.chitchat.server.enums.Gender;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level=AccessLevel.PRIVATE)
public class UserResponse {
     Long id;
     String email;
     String username;
     String firstName;
     String lastName;
     String location;
     String bio;
     String job;
     Set<String> authorities;
     String phone;
     
     String avatarPublicId;
     String avatarUrl;
     String coverPhotoPublicId;
     String coverPhotoUrl;

     Long conversationId;

     LocalDate dob;
     Instant createdAt;
     Instant updatedAt;
     
     Gender gender;

     boolean isFriend;

     int friendNum;
     Long mutualFriendsNum;

     // social media links
     String facebook;
     String twitter;
     String instagram;
     String linkedin;
     String youtube;
     String github;
     String tiktok;
     String discord;
}
