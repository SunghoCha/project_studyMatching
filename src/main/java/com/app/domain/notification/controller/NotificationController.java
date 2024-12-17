package com.app.domain.notification.controller;

import com.app.domain.notification.dto.NotificationListResponse;
import com.app.domain.notification.service.NotificationService;
import com.app.global.config.auth.LoginUser;
import com.app.global.config.auth.dto.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<NotificationListResponse> getNotifications(@LoginUser CurrentUser currentUser) {
        NotificationListResponse response = notificationService.getNotifications(currentUser.getId());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/old")
    public ResponseEntity<NotificationListResponse> getOldNotifications(@LoginUser CurrentUser currentUser) {
        NotificationListResponse response = notificationService.getOldNotification(currentUser.getId());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/check/{notificationId}")
    public ResponseEntity<Void> updateNotificationCheck(@LoginUser CurrentUser currentUser,
                                                        @PathVariable("notificationId") Long notificationId) {
        notificationService.updateNotificationCheck(currentUser.getId(), notificationId);

        return ResponseEntity.ok().build();
    }
}
