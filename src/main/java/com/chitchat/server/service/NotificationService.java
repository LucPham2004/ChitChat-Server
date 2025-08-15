package com.chitchat.server.service;

import com.chitchat.server.entity.Notification;
import com.chitchat.server.enums.NotificationType;
import org.springframework.data.domain.Page;

public interface NotificationService {
    Page<Notification> getUserNotifications(Long userId, int pageNum);

    // Create Notification
    Notification notifyUser(String username, String message, NotificationType type);

    // Delete Notification
    void deleteNotify(Long notifyId);
}
