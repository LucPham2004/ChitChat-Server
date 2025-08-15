package com.chitchat.server.mapper;

import com.chitchat.server.dto.response.FriendShipResponse;
import com.chitchat.server.entity.Friendship;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal = true)
public class FriendshipMapper {
    
    public FriendShipResponse toFriendShipResponse(Friendship friendship) {
        FriendShipResponse response = new FriendShipResponse();
        response.setFriendshipId(friendship.getId());
        response.setReceiverId(friendship.getRecipient().getId());
        response.setRequesterId(friendship.getSender().getId());
        response.setStatus(friendship.getStatus());

        return response;
    }
}
