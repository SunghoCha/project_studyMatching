package com.app.domain.notification.repository;

import com.app.domain.notification.Notification;

import java.util.List;
import java.util.Optional;

public interface NotificationRepositoryCustom {

    List<Notification> findOldNotificationByUserId(Long userId);

    Optional<Notification> findByIdAndUserId(Long id, Long userId);
}
