package com.app.domain.notification.controller;

import com.app.WithAccount;
import com.app.domain.notification.Notification;
import com.app.domain.notification.NotificationType;
import com.app.domain.notification.repository.NotificationRepository;
import com.app.domain.user.User;
import com.app.domain.user.repository.UserRepository;
import com.app.global.error.ErrorCode;
import com.app.global.error.exception.UserNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class NotificationControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithAccount
    @DisplayName("전체 알림 조회 성공 테스트")
    void getNotifications() throws Exception {
        // given
        User user = userRepository.findByEmail("test@gmail.com").orElseThrow(UserNotFoundException::new);

            Notification notification1 = Notification.builder()
                    .user(user)
                    .title("읽은 알림" + 1)
                    .message("읽은 알림 메시지" + 1)
                    .link("읽은 링크" + 1)
                    .checked(true)
                    .notificationType(NotificationType.STUDY_CREATED)
                    .build();
            notificationRepository.save(notification1);

            Notification notification2 = Notification.builder()
                    .user(user)
                    .title("읽지 않은 알림" + 1)
                    .message("읽지 않은 알림 메시지" + 1)
                    .link("읽지 않은 링크" + 1)
                    .checked(false)
                    .notificationType(NotificationType.STUDY_CREATED)
                    .build();
            notificationRepository.save(notification2);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.get("/notifications"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.studyCreateNotifications[0].title").value("읽은 알림1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.studyCreateNotifications[0].message").value("읽은 알림 메시지1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.studyCreateNotifications[0].link").value("읽은 링크1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.studyCreateNotifications[1].title").value("읽지 않은 알림1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.studyCreateNotifications[1].message").value("읽지 않은 알림 메시지1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.studyCreateNotifications[1].link").value("읽지 않은 링크1"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("읽은 알림 조회 성공 테스트")
    void getOldNotifications() throws Exception {
        // given
        User user = userRepository.findByEmail("test@gmail.com").orElseThrow(UserNotFoundException::new);

        Notification notification1 = Notification.builder()
                .user(user)
                .title("읽은 알림" + 1)
                .message("읽은 알림 메시지" + 1)
                .link("읽은 링크" + 1)
                .checked(true)
                .notificationType(NotificationType.STUDY_CREATED)
                .build();
        notificationRepository.save(notification1);

        Notification notification2 = Notification.builder()
                .user(user)
                .title("읽지 않은 알림" + 1)
                .message("읽지 않은 알림 메시지" + 1)
                .link("읽지 않은 링크" + 1)
                .checked(false)
                .notificationType(NotificationType.STUDY_CREATED)
                .build();
        notificationRepository.save(notification2);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.get("/notifications/old"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.studyCreateNotifications[0].title").value("읽은 알림1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.studyCreateNotifications[0].message").value("읽은 알림 메시지1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.studyCreateNotifications[0].link").value("읽은 링크1"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("알림 상태 수정 테스트")
    void updateNotificationCheck() throws Exception {
        // given
        User user = userRepository.findByEmail("test@gmail.com").orElseThrow(UserNotFoundException::new);

        Notification notification = Notification.builder()
                .user(user)
                .title("읽지 않은 알림" + 1)
                .message("읽지 않은 알림 메시지" + 1)
                .link("읽지 않은 링크" + 1)
                .checked(true)
                .notificationType(NotificationType.STUDY_CREATED)
                .build();
        Notification savedNotification = notificationRepository.save(notification);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.patch("/notifications/check/{notificationId}", notification.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("잘못된 알림 id값으로 요청하면 예외 발생")
    void updateNotificationCheck_with_wrong_input() throws Exception {
        // given
        User user = userRepository.findByEmail("test@gmail.com").orElseThrow(UserNotFoundException::new);

        Notification notification = Notification.builder()
                .user(user)
                .title("읽지 않은 알림" + 1)
                .message("읽지 않은 알림 메시지" + 1)
                .link("읽지 않은 링크" + 1)
                .checked(true)
                .notificationType(NotificationType.STUDY_CREATED)
                .build();
        Notification savedNotification = notificationRepository.save(notification);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.patch("/notifications/check/{notificationId}", 12345123123L))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value(ErrorCode.NOTIFICATION_NOT_EXIST.getErrorCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage")
                        .value(ErrorCode.NOTIFICATION_NOT_EXIST.getMessage()))
                .andDo(MockMvcResultHandlers.print());
    }

}