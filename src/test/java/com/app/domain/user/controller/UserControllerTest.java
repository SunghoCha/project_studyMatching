package com.app.domain.user.controller;

import com.app.WithAccount;
import com.app.domain.notification.Notification;
import com.app.domain.user.dto.UserNotificationUpdateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @WithAccount
    @DisplayName("알림 설정 변경 성공 테스트")
    void updateNotificationSettings() throws Exception {

        // given
        UserNotificationUpdateRequest request = UserNotificationUpdateRequest.builder()
                .studyCreatedByEmail(false)
                .studyCreatedByWeb(false)
                .studyEnrollmentResultByEmail(false)
                .studyEnrollmentResultByWeb(false)
                .studyUpdatedByEmail(false)
                .studyUpdatedByWeb(false)
                .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.patch("/user/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }
}