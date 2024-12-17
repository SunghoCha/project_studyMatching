package com.app.domain.notification.repository;

import com.app.WithAccount;
import com.app.domain.notification.Notification;
import com.app.domain.notification.NotificationType;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class NotificationRepositoryImplTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    @WithAccount
    @DisplayName("읽은 알림 조회 성공 테스트")
    void findOldNotificationByUserId() {
        // given
        User user = userRepository.findByEmail("test@gmail.com").orElseThrow(UserNotFoundException::new);

        Notification notification1 = Notification.builder()
                .user(user)
                .title("읽은 알림")
                .message("읽은 알림 메시지")
                .link("link1")
                .checked(true)
                .notificationType(NotificationType.STUDY_CREATED)
                .build();

        Notification notification2 = Notification.builder()
                .user(user)
                .title("읽지 않은 알림")
                .message("읽지 않은 알림 메시지")
                .link("link1")
                .checked(false)
                .notificationType(NotificationType.STUDY_CREATED)
                .build();

        notificationRepository.saveAll(List.of(notification1, notification2));

        // when
        List<Notification> notifications = notificationRepository.findOldNotificationByUserId(user.getId());

        // then
        Assertions.assertThat(notifications).hasSize(1).extracting("title", "message", "checked")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("읽은 알림", "읽은 알림 메시지", true));


    }

}