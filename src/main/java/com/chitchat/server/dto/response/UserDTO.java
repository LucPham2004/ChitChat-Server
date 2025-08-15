package com.chitchat.server.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level=AccessLevel.PRIVATE)
public class UserDTO {
    Long id;
    String firstName;
    String lastName;
    String location;
    String job;
    String avatarPublicId;
    String avatarUrl;
    boolean isFriend;
    boolean isFriendRequestSent;
    int friendNum;
    Long mutualFriendsNum;

    Long conversationId;
}
