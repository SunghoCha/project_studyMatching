package com.app.domain.notification.service;

import com.app.domain.notification.Notification;
import com.app.domain.notification.NotificationType;
import com.app.domain.notification.dto.NotificationListResponse;
import com.app.domain.notification.dto.NotificationResponse;
import com.app.domain.notification.repository.NotificationRepository;
import com.app.global.error.exception.InvalidNotificationException;
import com.app.global.error.exception.InvalidNotificationTypeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationListResponse getNotifications(Long userId) {
        List<NotificationResponse> studyCreateNotifications = new ArrayList<>();
        List<NotificationResponse> eventEnrollmentNotifications = new ArrayList<>();
        List<NotificationResponse> studyUpdateNotifications = new ArrayList<>();

        List<Notification> notifications = notificationRepository.findAllByUserId(userId);
        categoriesNotifications(notifications, studyCreateNotifications, eventEnrollmentNotifications, studyUpdateNotifications);

        return NotificationListResponse.builder()
                .studyCreateNotifications(studyCreateNotifications)
                .eventEnrollmentNotifications(eventEnrollmentNotifications)
                .studyUpdateNotifications(studyUpdateNotifications)
                .build();
    }

    public NotificationListResponse getOldNotification(Long userId) {
        List<NotificationResponse> studyCreateNotifications = new ArrayList<>();
        List<NotificationResponse> eventEnrollmentNotifications = new ArrayList<>();
        List<NotificationResponse> studyUpdateNotifications = new ArrayList<>();

        List<Notification> notifications = notificationRepository.findOldNotificationByUserId(userId);
        categoriesNotifications(notifications, studyCreateNotifications, eventEnrollmentNotifications, studyUpdateNotifications);

        return NotificationListResponse.builder()
                .studyCreateNotifications(studyCreateNotifications)
                .eventEnrollmentNotifications(eventEnrollmentNotifications)
                .studyUpdateNotifications(studyUpdateNotifications)
                .build();
    }

    private static void categoriesNotifications(List<Notification> notifications, List<NotificationResponse> studyCreateNotifications, List<NotificationResponse> eventEnrollmentNotifications, List<NotificationResponse> studyUpdateNotifications) {
        notifications.forEach(notification -> {
            if (notification.getNotificationType().equals(NotificationType.STUDY_CREATED)) {
                studyCreateNotifications.add(NotificationResponse.of(notification));

            } else if (notification.getNotificationType().equals(NotificationType.STUDY_EVENT_ENROLLMENT)) {
                eventEnrollmentNotifications.add(NotificationResponse.of(notification));

            } else if (notification.getNotificationType().equals(NotificationType.STUDY_UPDATED)) {
                studyUpdateNotifications.add(NotificationResponse.of(notification));

            } else {
                throw new InvalidNotificationTypeException();
            }
        });
    }

    public void updateNotificationCheck(Long userId, Long notificationId) {

        Notification notification = notificationRepository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(InvalidNotificationException::new);

        notification.checked();
    }
}
