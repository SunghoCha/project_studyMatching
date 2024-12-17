package com.app.domain.notification.repository;

import com.app.domain.notification.Notification;
import com.app.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationRepositoryCustom {
    List<Notification> findAllByUserId(Long userId);
}
