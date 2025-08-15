package com.chitchat.server.service.impl;

import com.chitchat.server.entity.Notification;
import com.chitchat.server.entity.User;
import com.chitchat.server.enums.NotificationType;
import com.chitchat.server.exception.AppException;
import com.chitchat.server.exception.ErrorCode;
import com.chitchat.server.repository.NotificationRepository;
import com.chitchat.server.service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
public class NotificationServiceImpl implements NotificationService {

    //SimpMessagingTemplate messagingTemplate;
    NotificationRepository notificationRepository;
    UserServiceImpl userService;

    static int NOTIFY_PER_PAGE = 10;

    // Get User Notifications
    public Page<Notification> getUserNotifications(Long userId, int pageNum) {
        User user = userService.findById(userId).orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_EXISTED));
        if(user == null) {
            throw new AppException(ErrorCode.ENTITY_NOT_EXISTED);
        }
        Pageable pageable = PageRequest.of(pageNum, NOTIFY_PER_PAGE);
        return notificationRepository.findByUserId(userId, pageable);
    }

    // Create Notification
    public Notification notifyUser(String username, String message, NotificationType type) {
        //
        return null;
    }

    // Delete Notification
    public void deleteNotify(Long notifyId) {
        if (!notificationRepository.existsById(notifyId)) {
            throw new AppException(ErrorCode.ENTITY_NOT_EXISTED);
        }
        notificationRepository.deleteById(notifyId);
    }
}
