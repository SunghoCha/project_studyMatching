package com.app.domain.notification.service;

import com.app.WithAccount;
import com.app.domain.notification.Notification;
import com.app.domain.notification.NotificationType;
import com.app.domain.notification.repository.NotificationRepository;
import com.app.domain.user.User;
import com.app.domain.user.repository.UserRepository;
import com.app.global.error.exception.UserNotFoundException;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class NotificationServiceTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    NotificationRepository notificationRepository;

    @Test
    @WithAccount
    @DisplayName("전체 알림 조회 성공 테스트")
    void getNotifications() {
        // given
        User user = userRepository.findByEmail("test@gmail.com").orElseThrow(UserNotFoundException::new);

        List<Notification> list = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Notification notification = Notification.builder()
                    .user(user)
                    .title("읽지 않은 알림" + i)
                    .message("읽지 않은 알림 메시지" + i)
                    .link("읽지 않은 링크" + i)
                    .checked(false)
                    .notificationType(NotificationType.STUDY_CREATED)
                    .build();
            list.add(notification);
        }
        notificationRepository.saveAll(list);

        // when
        List<Notification> notifications = notificationRepository.findAllByUserId(user.getId());

        // then
        Assertions.assertThat(notifications).hasSize(5)
                .extracting("title", "message", "link", "checked", "notificationType")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("읽지 않은 알림1", "읽지 않은 알림 메시지1", "읽지 않은 링크1", false, NotificationType.STUDY_CREATED),
                        Tuple.tuple("읽지 않은 알림2", "읽지 않은 알림 메시지2", "읽지 않은 링크2", false, NotificationType.STUDY_CREATED),
                        Tuple.tuple("읽지 않은 알림3", "읽지 않은 알림 메시지3", "읽지 않은 링크3", false, NotificationType.STUDY_CREATED),
                        Tuple.tuple("읽지 않은 알림4", "읽지 않은 알림 메시지4", "읽지 않은 링크4", false, NotificationType.STUDY_CREATED),
                        Tuple.tuple("읽지 않은 알림5", "읽지 않은 알림 메시지5", "읽지 않은 링크5", false, NotificationType.STUDY_CREATED)
                );
    }

    @Test
    @WithAccount
    @DisplayName("읽은 알림 조회 성공 테스트")
    void getOldNotification() {
        // given
        User user = userRepository.findByEmail("test@gmail.com").orElseThrow(UserNotFoundException::new);

        List<Notification> list = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Notification notification = Notification.builder()
                    .user(user)
                    .title("읽지 않은 알림" + i)
                    .message("읽지 않은 알림 메시지" + i)
                    .link("읽지 않은 링크" + i)
                    .checked(false)
                    .notificationType(NotificationType.STUDY_CREATED)
                    .build();
            list.add(notification);
        }
        notificationRepository.saveAll(list);

        List<Notification> checkedList = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Notification notification = Notification.builder()
                    .user(user)
                    .title("읽은 알림" + i)
                    .message("읽은 알림 메시지" + i)
                    .link("읽은 링크" + i)
                    .checked(true)
                    .notificationType(NotificationType.STUDY_CREATED)
                    .build();
            list.add(notification);
        }
        notificationRepository.saveAll(list);

        // when
        List<Notification> notifications = notificationRepository.findOldNotificationByUserId(user.getId());

        // then
        Assertions.assertThat(notifications).hasSize(3)
                .extracting("title", "message", "link", "checked", "notificationType")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("읽은 알림1", "읽은 알림 메시지1", "읽은 링크1", true, NotificationType.STUDY_CREATED),
                        Tuple.tuple("읽은 알림2", "읽은 알림 메시지2", "읽은 링크2", true, NotificationType.STUDY_CREATED),
                        Tuple.tuple("읽은 알림3", "읽은 알림 메시지3", "읽은 링크3", true, NotificationType.STUDY_CREATED)
                );
    }
}