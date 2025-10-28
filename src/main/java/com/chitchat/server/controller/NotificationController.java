package com.chitchat.server.controller;

import com.chitchat.server.dto.response.ApiResponse;
import com.chitchat.server.entity.Notification;
import com.chitchat.server.service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notify")
@RequiredArgsConstructor
@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
public class NotificationController {

    //SimpMessagingTemplate messagingTemplate;
    NotificationService notificationService;

    // Get User Notifications
    @GetMapping("/get")
    public ApiResponse<Page<Notification>> getUserNotifications(
            @RequestParam String userId,
            @RequestParam(defaultValue = "0") int pageNum) {
        Page<Notification> notifications = notificationService.getUserNotifications(userId, pageNum);
        return ApiResponse.<Page<Notification>>builder()
            .code(1000)
            .message("Get notifications successfully")
            .result(notifications)
            .build();
    }

    // @MessageMapping("/send-notification")
    // public void sendNotification(Notification notification) {
    //     String username = notification.getUser().getUsername(); 
    //     messagingTemplate.convertAndSendToUser(username, "/queue/notifications", notification);
    // }

    // Delete Notification
    @DeleteMapping("/delete/{notifyId}")
    public ApiResponse<String> deleteNotify(@PathVariable Long notifyId) {
        notificationService.deleteNotify(notifyId);
        return ApiResponse.<String>builder()
            .code(1000)
            .message("Delete notification successfully")
            .result("")
            .build();
    }
}