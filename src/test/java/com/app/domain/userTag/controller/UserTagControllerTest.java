package com.app.domain.userTag.controller;

import com.app.WithAccount;
import com.app.domain.tag.Tag;
import com.app.domain.tag.repository.TagRepository;
import com.app.domain.user.userTag.dto.UserTagUpdateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
@ActiveProfiles("test")
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class UserTagControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    TagRepository tagRepository;

    @BeforeEach
    void setup() {
        tagRepository.deleteAll();
        tagRepository.save(new Tag("SPRING"));
        tagRepository.save(new Tag("JAVA"));
        tagRepository.save(new Tag("VUE"));
    }

    @Test
    @WithAccount()
    @DisplayName("userTag 수정 성공 테스트")
    void tag_edit_UserTags_with_correct_input() throws Exception {
        // given
        OAuth2User oAuth2User = (DefaultOAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long id = (Long) oAuth2User.getAttributes().get("id");

        UserTagUpdateRequest updateRequest = UserTagUpdateRequest.builder()
                .tags(Set.of("SPRING", "JAVA"))
                .build();

        // expected
        mockMvc.perform(MockMvcRequestBuilders.patch("/user-tag", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userTags", Matchers.containsInAnyOrder("SPRING","JAVA")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount()
    @DisplayName("잘못된 tag로 userTag 수정 요청시 보내면 예외 발생")
    void tag_edit_UserTags_with_wrong_input() throws Exception {
        // given
        OAuth2User oAuth2User = (DefaultOAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long id = (Long) oAuth2User.getAttributes().get("id");

        UserTagUpdateRequest updateRequest = UserTagUpdateRequest.builder()
                .tags(Set.of("SPRING2", "JAVA"))
                .build();

        // expected
        mockMvc.perform(MockMvcRequestBuilders.patch("/user-tag", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }
}