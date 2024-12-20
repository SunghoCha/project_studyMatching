package com.app.domain.tag.controller;

import com.app.WithAccount;
import com.app.domain.tag.Tag;
import com.app.domain.tag.repository.TagRepository;
import com.app.domain.tag.service.TagService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
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

import java.util.List;

import static com.app.TestUtils.createTags;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class TagControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    TagService tagService;

    @Autowired
    TagRepository tagRepository;

    @Test
    @WithAccount
    @DisplayName("전체 태그 조회하면 tag title 리스트 반환")
    void getTags_with_correct_input() throws Exception {
        // given
        List<Tag> savedTags = tagRepository.saveAll(createTags(3));

        // expected
        mockMvc.perform(MockMvcRequestBuilders.get("/tag"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[*].tag")
                        .value(Matchers.containsInAnyOrder("tag1", "tag2", "tag3")))
                .andDo(MockMvcResultHandlers.print());
    }


}