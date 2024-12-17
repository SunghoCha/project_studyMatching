package com.app.domain.notification.dto;

import com.app.domain.notification.Notification;
import com.app.domain.notification.NotificationType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class NotificationResponse {

    private Long id;
    private String title;
    private String message;
    private String link;
    private boolean checked;
    private LocalDateTime createdDateTime;
    private NotificationType notificationType;

    @Builder
    public NotificationResponse(Long id, String title, String message, String link, boolean checked, LocalDateTime createdDateTime, NotificationType notificationType) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.link = link;
        this.checked = checked;
        this.createdDateTime = createdDateTime;
        this.notificationType = notificationType;
    }

    public static NotificationResponse of(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .link(notification.getLink())
                .checked(notification.isChecked())
                .createdDateTime(notification.getCreateTime())
                .notificationType(notification.getNotificationType())
                .build();
    }
}
