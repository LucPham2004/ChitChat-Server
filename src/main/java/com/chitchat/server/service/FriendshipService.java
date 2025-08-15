package com.chitchat.server.service;

import com.chitchat.server.entity.Friendship;
import com.chitchat.server.enums.FriendshipStatus;

public interface FriendshipService {
    Friendship getFriendStatus(Long requesterId, Long receiverId);

    Friendship sendFriendRequest(Long requesterId, Long receiverId);

    void deleteFriendShip(Long iselfId, Long otherId);

    Friendship editFriendShipStatus(Long selfId, Long otherId, FriendshipStatus status);
}
