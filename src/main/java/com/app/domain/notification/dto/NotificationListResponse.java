package com.app.domain.notification.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;

@Getter @Setter
public class NotificationListResponse {

    private List<NotificationResponse> studyCreateNotifications;
    private List<NotificationResponse> eventEnrollmentNotifications;
    private List<NotificationResponse> studyUpdateNotifications;

    @Builder
    public NotificationListResponse(List<NotificationResponse> studyCreateNotifications,
                                    List<NotificationResponse> eventEnrollmentNotifications,
                                    List<NotificationResponse> studyUpdateNotifications) {
        this.studyCreateNotifications = studyCreateNotifications;
        this.eventEnrollmentNotifications = eventEnrollmentNotifications;
        this.studyUpdateNotifications = studyUpdateNotifications;
    }
}
