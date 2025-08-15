package com.chitchat.server.dto.response;

import com.chitchat.server.enums.FriendshipStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level=AccessLevel.PRIVATE)
public class FriendShipResponse {
    Long friendshipId;
    Long requesterId;
    Long receiverId;

    FriendshipStatus status;
}
