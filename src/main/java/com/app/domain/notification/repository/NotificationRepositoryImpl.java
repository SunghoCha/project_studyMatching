package com.app.domain.notification.repository;

import com.app.domain.notification.Notification;
import com.app.domain.notification.QNotification;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Notification> findOldNotificationByUserId(Long userId) {
        QNotification notification = QNotification.notification;

        return queryFactory
                .select(notification)
                .from(notification)
                .where(
                        notification.user.id.eq(userId),
                        notification.checked.isTrue())
                .fetch();
    }

    @Override
    public Optional<Notification> findByIdAndUserId(Long id, Long userId) {
        QNotification notification = QNotification.notification;

        return Optional.ofNullable(queryFactory
                .select(notification)
                .from(notification)
                .where(
                        notification.user.id.eq(userId),
                        notification.id.eq(id))
                .fetchOne());
    }
}
