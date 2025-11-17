package com.chitchat.server.service;

import com.chitchat.server.entity.Friendship;
import com.chitchat.server.enums.FriendshipStatus;

public interface FriendshipService {
    Friendship getFriendStatus(String requesterId, String receiverId);

    Friendship sendFriendRequest(String requesterId, String receiverId);

    void deleteFriendShip(String iselfId, String otherId);

    Friendship editFriendShipStatus(String selfId, String otherId, FriendshipStatus status);

    void unblockUser(String selfId, String otherId);
}
