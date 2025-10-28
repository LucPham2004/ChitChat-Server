package com.chitchat.server.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level=AccessLevel.PRIVATE)
public class ChatParticipants {
    String id;
    String username;
    String avatarPublicId;
    String avatarUrl;
    String firstName;
    String lastName;
}
