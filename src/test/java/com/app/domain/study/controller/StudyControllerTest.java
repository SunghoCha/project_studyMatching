package com.app.domain.study.controller;

import com.app.WithAccount;
import com.app.domain.common.dto.PagedResponse;
import com.app.domain.study.Study;
import com.app.domain.study.dto.StudyCreateRequest;
import com.app.domain.study.dto.StudyQueryResponse;
import com.app.domain.study.dto.StudyResponse;
import com.app.domain.study.repository.StudyRepository;
import com.app.domain.study.service.StudyService;
import com.app.domain.study.studyManager.StudyManager;
import com.app.domain.study.studyManager.repository.StudyManagerRepository;
import com.app.domain.study.studyMember.StudyMember;
import com.app.domain.study.studyMember.repository.StudyMemberRepository;
import com.app.domain.user.User;
import com.app.domain.user.constant.Role;
import com.app.domain.user.repository.UserRepository;
import com.app.global.config.auth.LoginUser;
import com.app.global.config.auth.dto.CurrentUser;
import com.app.global.error.ErrorCode;
import com.app.global.error.exception.StudyNotFoundException;
import com.app.global.error.exception.UserNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class StudyControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    StudyRepository studyRepository;

    @Autowired
    StudyManagerRepository studyManagerRepository;
    private StudyService studyService;
    @Autowired
    private StudyMemberRepository studyMemberRepository;

    @Test
    @WithAccount
    @DisplayName("스터디 생성 테스트")
    void create_study_with_correct_input() throws Exception {
        // given
        StudyCreateRequest request = StudyCreateRequest.builder()
                .path("test1")
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/study/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.path").value("test1"))
                .andExpect(jsonPath("$.title").value("테스트 스터디1"))
                .andExpect(jsonPath("$.shortDescription").value("짧은 글 설명1"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("path는 최소 두 글자 이상. 아닐시 예외 발생")
    void create_study_with_wrong_path() throws Exception {
        // given
        StudyCreateRequest request = StudyCreateRequest.builder()
                .path("t")
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/study/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode")
                        .value("400 BAD_REQUEST"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("title은 최대 15글자. 아닐시 예외 발생")
    void create_study_with_wrong_title() throws Exception {
        // given
        StudyCreateRequest request = StudyCreateRequest.builder()
                .path("test1")
                .title("1234567890124567")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/study/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode")
                        .value("400 BAD_REQUEST"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("스터디 조회 성공테스트")
    void get_study_with_correct_path() throws Exception {
        // given
        // study 세팅
        String path = "test";
        User user = userRepository.findByEmail("test@gmail.com").orElseThrow(UserNotFoundException::new);
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        StudyManager manager = StudyManager.createManager(user, study);
        studyManagerRepository.save(manager);
        study.addManager(manager);
        Study savedStudy = studyRepository.save(study);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.get("/study/{path}", path))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.path").value(path))
                .andExpect(jsonPath("$.title").value("테스트 스터디1"))
                .andExpect(jsonPath("$.shortDescription").value("짧은 글 설명1"))
                .andExpect(jsonPath("$.fullDescription").value("긴 글 설명1"))
                .andExpect(jsonPath("$.published").value(false))
                .andExpect(jsonPath("$.closed").value(false))
                .andExpect(jsonPath("$.recruiting").value(false))
                .andExpect(jsonPath("$.tags").isEmpty())
                .andExpect(jsonPath("$.zones").isEmpty())
                .andExpect(jsonPath("$.members").isEmpty())
                .andExpect(jsonPath("$.managers[0].name").value("testName"))
                .andExpect(jsonPath("$.managers[0].email").value("test@gmail.com"))
                .andExpect(jsonPath("$.isMember").value(false))
                .andExpect(jsonPath("$.isJoinable").value(false))
                .andExpect(jsonPath("$.isManager").value(true))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("잘못된 path로 스터디 조회 요청시 예외 발생")
    void get_study_with_wrong_path() throws Exception {
        // given
        // study 세팅
        String path = "test";
        User user = userRepository.findByEmail("test@gmail.com").orElseThrow(UserNotFoundException::new);
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        StudyManager manager = StudyManager.createManager(user, study);
        studyManagerRepository.save(manager);
        study.addManager(manager);
        Study savedStudy = studyRepository.save(study);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.get("/study/{path}", "wrong"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode")
                        .value(ErrorCode.STUDY_NOT_EXISTS.getErrorCode()))
                .andExpect(jsonPath("$.errorMessage")
                        .value(ErrorCode.STUDY_NOT_EXISTS.getMessage()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("전체 스터디 목록 조회 성공 테스트")
    void get_studies_with_correct_input() throws Exception {
        // given
        // 스터디 세팅
        User user = userRepository.findByEmail("test@gmail.com").orElseThrow(UserNotFoundException::new);
        for (int i = 1; i <= 20; i++ ) {
            Study study = Study.builder()
                    .path("path" + i)
                    .title("테스트 스터디" + i)
                    .shortDescription("짧은 글 설명" + i)
                    .fullDescription("긴 글 설명" + i)
                    .build();
            StudyManager manager = StudyManager.createManager(user, study);
            studyManagerRepository.save(manager);
            study.addManager(manager);
            studyRepository.save(study);
        }

        // expected
        mockMvc.perform(MockMvcRequestBuilders.get("/study/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(9))
                .andExpect(jsonPath("$.currentPage").value(1))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.totalCount").value(20))
                .andExpect(jsonPath("$.size").value(9))
                .andExpect(jsonPath("$.content[0].path").value("path1"))
                .andExpect(jsonPath("$.content[0].title").value("테스트 스터디1"))
                .andExpect(jsonPath("$.content[0].shortDescription").value("짧은 글 설명1"))
                .andExpect(jsonPath("$.content[8].path").value("path9"))
                .andExpect(jsonPath("$.content[8].title").value("테스트 스터디9"))
                .andExpect(jsonPath("$.content[8].shortDescription").value("짧은 글 설명9"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("가입 스터디 목록 조회 성공 테스트")
    void get_joined_studies_with_correct_input() throws Exception {
        // given
        // 스터디 세팅
        User user = userRepository.findByEmail("test@gmail.com").orElseThrow(UserNotFoundException::new);
        for (int i = 1; i <= 20; i++ ) {
            Study study = Study.builder()
                    .path("path" + i)
                    .title("테스트 스터디" + i)
                    .shortDescription("짧은 글 설명" + i)
                    .fullDescription("긴 글 설명" + i)
                    .build();
            StudyManager manager = StudyManager.createManager(user, study);
            studyManagerRepository.save(manager);
            study.addManager(manager);
            studyRepository.save(study);
        }

        User member = User.builder()
                .name("멤버1")
                .email("member@example.com")
                .role(Role.GUEST)
                .build();
        User savedMember = userRepository.save(member);

        // 스터디 가입 세팅
        for (int i = 1; i <= 3; i++) {
            Study study = studyRepository.findByPath("path" + i).orElseThrow(StudyNotFoundException::new);
            StudyMember studyMember = StudyMember.createMember(member, study);

            StudyMember savedStudyMember = studyMemberRepository.save(studyMember);
            study.addMember(savedStudyMember);
        }


        // expected
        mockMvc.perform(MockMvcRequestBuilders.get("/study/my-joined-study-list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(9))
                .andExpect(jsonPath("$.currentPage").value(1))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.totalCount").value(20))
                .andExpect(jsonPath("$.size").value(9))
                .andExpect(jsonPath("$.content[0].path").value("path1"))
                .andExpect(jsonPath("$.content[0].title").value("테스트 스터디1"))
                .andExpect(jsonPath("$.content[0].shortDescription").value("짧은 글 설명1"))
                .andExpect(jsonPath("$.content[8].path").value("path9"))
                .andExpect(jsonPath("$.content[8].title").value("테스트 스터디9"))
                .andExpect(jsonPath("$.content[8].shortDescription").value("짧은 글 설명9"))
                .andDo(MockMvcResultHandlers.print());
    }
}