package com.app.domain.study.controller;

import com.app.WithAccount;
import com.app.domain.study.dto.StudyCreateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assert;
import org.assertj.core.api.Assertions;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
class StudyControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @WithAccount
    @DisplayName("스터디 생성 테스트")
    void create_study_with_correct_input() throws Exception {
        // given
        StudyCreateRequest request = StudyCreateRequest.builder()
                .path("test1")
                .title("테스트 스터디")
                .shortDescription("짧은 글 설명")
                .fullDescription("긴 글 설명")
                .build();

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post("/study/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.path").value("test1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("테스트 스터디"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.shortDescription").value("짧은 글 설명"))
                .andDo(MockMvcResultHandlers.print());

        // when

        // then
    }

}