package com.app.domain.study.controller;

import com.app.WithAccount;
import com.app.domain.study.Study;
import com.app.domain.study.dto.studySetting.StudyPathUpdateRequest;
import com.app.domain.study.dto.studySetting.StudyTitleUpdateRequest;
import com.app.domain.study.repository.StudyRepository;
import com.app.domain.study.studyManager.StudyManager;
import com.app.domain.study.studyManager.repository.StudyManagerRepository;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class StudySettingControllerTest {

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

    @Test
    @WithAccount
    @DisplayName("스터디 publish 성공 테스트")
    void publish_study_with_correct_input() throws Exception {
        // given
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
        mockMvc.perform(MockMvcRequestBuilders.post("/study/{path}/setting/publish", path))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("published").isBoolean())
                .andExpect(MockMvcResultMatchers.jsonPath("published").value(true))
                .andDo(MockMvcResultHandlers.print());

    }

    @Test
    @WithAccount
    @DisplayName("잘못된 경로로 스터디 publish 시도하면 예외 발생")
    void publish_study_with_wrong_path() throws Exception {
        // given
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
        mockMvc.perform(MockMvcRequestBuilders.post("/study/{path}/setting/publish", "wrong"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.errorCode")
                        .value(ErrorCode.STUDY_NOT_EXISTS.getErrorCode()))
                .andExpect(jsonPath("$.errorMessage")
                        .value(ErrorCode.STUDY_NOT_EXISTS.getMessage()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("이미 publish된 스터디를 publish 시도하면 예외 발생")
    void publish_study_with_published_study() throws Exception {
        // given
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
        study.publish();
        Study savedStudy = studyRepository.save(study);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/study/{path}/setting/publish", path))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.errorCode")
                        .value(ErrorCode.INVALID_STUDY_PUBLISH_STATE.getErrorCode()))
                .andExpect(jsonPath("$.errorMessage")
                        .value(ErrorCode.INVALID_STUDY_PUBLISH_STATE.getMessage()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("이미 closed된 스터디를 publish 시도하면 예외 발생")
    void publish_study_with_closed_study() throws Exception {
        // given
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
        study.publish();
        study.close();
        Study savedStudy = studyRepository.save(study);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/study/{path}/setting/publish", path))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.errorCode")
                        .value(ErrorCode.INVALID_STUDY_PUBLISH_STATE.getErrorCode()))
                .andExpect(jsonPath("$.errorMessage")
                        .value(ErrorCode.INVALID_STUDY_PUBLISH_STATE.getMessage()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("스터디 close 성공 테스트")
    void close_study_with_correct_input() throws Exception {
        // given
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
        study.publish();
        Study savedStudy = studyRepository.save(study);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/study/{path}/setting/close", path))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("closed").isBoolean())
                .andExpect(MockMvcResultMatchers.jsonPath("closed").value(true))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("잘못된 경로로 스터디 close 시도하면 예외 발생")
    void close_study_with_wrong_path() throws Exception {
        // given
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
        mockMvc.perform(MockMvcRequestBuilders.post("/study/{path}/setting/publish", "wrong"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.errorCode")
                        .value(ErrorCode.STUDY_NOT_EXISTS.getErrorCode()))
                .andExpect(jsonPath("$.errorMessage")
                        .value(ErrorCode.STUDY_NOT_EXISTS.getMessage()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("이미 closed된 스터디를 publish 시도하면 예외 발생")
    void close_study_with_closed_study() throws Exception {
        // given
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
        study.publish();
        study.close();
        Study savedStudy = studyRepository.save(study);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/study/{path}/setting/close", path))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.errorCode")
                        .value(ErrorCode.INVALID_STUDY_CLOSE_STATE.getErrorCode()))
                .andExpect(jsonPath("$.errorMessage")
                        .value(ErrorCode.INVALID_STUDY_CLOSE_STATE.getMessage()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("아직 publish되지 않은 스터디를 close시도하면 예외 발생")
    void close_study_with_not_published_study() throws Exception {
        // given
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
        mockMvc.perform(MockMvcRequestBuilders.post("/study/{path}/setting/close", path))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.errorCode")
                        .value(ErrorCode.INVALID_STUDY_CLOSE_STATE.getErrorCode()))
                .andExpect(jsonPath("$.errorMessage")
                        .value(ErrorCode.INVALID_STUDY_CLOSE_STATE.getMessage()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("스터디 recruit 시작 성공 테스트")
    void start_recruit_study_with_correct_input() throws Exception {
        // given
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
        study.publish();
        Study savedStudy = studyRepository.save(study);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/study/{path}/setting/recruit/start", path))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("recruiting").isBoolean())
                .andExpect(MockMvcResultMatchers.jsonPath("recruiting").value(true))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("이미 recruiting했던 스터디라도 1시간 이상 지났으면 다시 recruting 가능")
    void start_recruit_study_after_1Hour() throws Exception {
        // given
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
        study.publish();

        Clock startOffsetClock = Clock.offset(Clock.systemDefaultZone(), Duration.ofMinutes(-122));
        study.startRecruit(LocalDateTime.now(startOffsetClock));

        Clock stopOffsetClock = Clock.offset(Clock.systemDefaultZone(), Duration.ofMinutes(-61));
        study.stopRecruit(LocalDateTime.now(stopOffsetClock));


        Study savedStudy = studyRepository.save(study);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/study/{path}/setting/recruit/start", path))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("recruiting").isBoolean())
                .andExpect(MockMvcResultMatchers.jsonPath("recruiting").value(true))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("이미 recruiting했던 스터디라도 1시간 이상 지나지않으면 다시 recruting 불가능")
    void start_recruit_study_before_1Hour() throws Exception {
        // given
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
        study.publish();

        Clock offsetClock = Clock.offset(Clock.systemDefaultZone(), Duration.ofMinutes(-59));
        study.startRecruit(LocalDateTime.now(offsetClock));
        Study savedStudy = studyRepository.save(study);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/study/{path}/setting/recruit/start", path))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.errorCode")
                        .value(ErrorCode.INVALID_RECRUITMENT_STATE.getErrorCode()))
                .andExpect(jsonPath("$.errorMessage")
                        .value(ErrorCode.INVALID_RECRUITMENT_STATE.getMessage()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("잘못된 경로로 스터디 recruiting 시도하면 예외 발생")
    void start_recruit_study_wrong_path() throws Exception {
        // given
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
        study.publish();

        Clock offsetClock = Clock.offset(Clock.systemDefaultZone(), Duration.ofMinutes(-59));
        study.startRecruit(LocalDateTime.now(offsetClock));
        Study savedStudy = studyRepository.save(study);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/study/{path}/setting/recruit/start", "wrong"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.errorCode")
                        .value(ErrorCode.STUDY_NOT_EXISTS.getErrorCode()))
                .andExpect(jsonPath("$.errorMessage")
                        .value(ErrorCode.STUDY_NOT_EXISTS.getMessage()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("아직 publish되지 않은 study를 recruit 시작하면 예외 발생")
    void start_recruit_study_before_publish() throws Exception {
        // given
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
        mockMvc.perform(MockMvcRequestBuilders.post("/study/{path}/setting/recruit/start", path))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.errorCode")
                        .value(ErrorCode.INVALID_RECRUITMENT_STATE.getErrorCode()))
                .andExpect(jsonPath("$.errorMessage")
                        .value(ErrorCode.INVALID_RECRUITMENT_STATE.getMessage()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("종료된 study를 recruit 시작하면 예외 발생")
    void start_recruit_study_already_closed() throws Exception {
        // given
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
        study.publish();
        study.close();

        Study savedStudy = studyRepository.save(study);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/study/{path}/setting/recruit/start", path))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.errorCode")
                        .value(ErrorCode.INVALID_RECRUITMENT_STATE.getErrorCode()))
                .andExpect(jsonPath("$.errorMessage")
                        .value(ErrorCode.INVALID_RECRUITMENT_STATE.getMessage()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("이미 recruit 중인 study를 recruit 시작하면 예외 발생")
    void start_recruit_study_already_recruiting() throws Exception {
        // given
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
        study.publish();

        Clock offsetClock = Clock.offset(Clock.systemDefaultZone(), Duration.ofMinutes(-60));
        study.startRecruit(LocalDateTime.now(offsetClock));

        Study savedStudy = studyRepository.save(study);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/study/{path}/setting/recruit/start", path))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.errorCode")
                        .value(ErrorCode.INVALID_RECRUITMENT_STATE.getErrorCode()))
                .andExpect(jsonPath("$.errorMessage")
                        .value(ErrorCode.INVALID_RECRUITMENT_STATE.getMessage()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("스터디 recruit 종료 성공 테스트")
    void stop_recruit_study_with_correct_input() throws Exception {
        // given
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
        study.publish();

        Clock offsetClock = Clock.offset(Clock.systemDefaultZone(), Duration.ofMinutes(-60));
        study.startRecruit(LocalDateTime.now(offsetClock));
        Study savedStudy = studyRepository.save(study);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/study/{path}/setting/recruit/stop", path))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("recruiting").isBoolean())
                .andExpect(MockMvcResultMatchers.jsonPath("recruiting").value(false))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("잘못된 경로로 스터디 recruiting 시도하면 예외 발생")
    void stop_recruit_study_wrong_path() throws Exception {
        // given
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
        study.publish();

        Clock offsetClock = Clock.offset(Clock.systemDefaultZone(), Duration.ofMinutes(-60));
        study.startRecruit(LocalDateTime.now(offsetClock));
        Study savedStudy = studyRepository.save(study);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/study/{path}/setting/recruit/start", "wrong"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.errorCode")
                        .value(ErrorCode.STUDY_NOT_EXISTS.getErrorCode()))
                .andExpect(jsonPath("$.errorMessage")
                        .value(ErrorCode.STUDY_NOT_EXISTS.getMessage()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("이미 recruit 종료된 study를 recruit 종료시도 하면 예외 발생")
    void stop_recruit_study_already_recruiting_stop() throws Exception {
        // given
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
        study.publish();

        Clock startClock = Clock.offset(Clock.systemDefaultZone(), Duration.ofMinutes(-90));
        study.startRecruit(LocalDateTime.now(startClock));

        Clock stopClock = Clock.offset(Clock.systemDefaultZone(), Duration.ofMinutes(-10));
        study.stopRecruit(LocalDateTime.now(stopClock));

        Study savedStudy = studyRepository.save(study);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/study/{path}/setting/recruit/start", path))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.errorCode")
                        .value(ErrorCode.INVALID_RECRUITMENT_STATE.getErrorCode()))
                .andExpect(jsonPath("$.errorMessage")
                        .value(ErrorCode.INVALID_RECRUITMENT_STATE.getMessage()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("종료된 study를 recruit 종료하면 예외 발생")
    void stop_recruit_study_already_closed() throws Exception {
        // given
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
        study.publish();
        study.close();

        Study savedStudy = studyRepository.save(study);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/study/{path}/setting/recruit/stop", path))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.errorCode")
                        .value(ErrorCode.INVALID_RECRUITMENT_STATE.getErrorCode()))
                .andExpect(jsonPath("$.errorMessage")
                        .value(ErrorCode.INVALID_RECRUITMENT_STATE.getMessage()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("스터디 path 수정 성공테스트")
    void update_study_path_correct_input() throws Exception {
        // given
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

        StudyPathUpdateRequest request = new StudyPathUpdateRequest("correct-path");
        String json = objectMapper.writeValueAsString(request);


        // expected
        mockMvc.perform(MockMvcRequestBuilders.patch("/study/{path}/setting/path", path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("path").value("correct-path"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("잘못된 경로로 스터디 path 수정 시도하면 예외 발생")
    void update_study_path_wrong_path() throws Exception {
        // given
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

        StudyPathUpdateRequest request = new StudyPathUpdateRequest("correct-path");
        String json = objectMapper.writeValueAsString(request);


        // expected
        mockMvc.perform(MockMvcRequestBuilders.patch("/study/{path}/setting/path", "wrong")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.errorCode")
                        .value(ErrorCode.STUDY_NOT_EXISTS.getErrorCode()))
                .andExpect(jsonPath("$.errorMessage")
                        .value(ErrorCode.STUDY_NOT_EXISTS.getMessage()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("이미 존재하는 path로 스터디 path 수정 시도하면 예외 발생")
    void update_study_path_wrong_input_path() throws Exception {
        // given
        String path = "test";
        String path2 = "test2";
        User user = userRepository.findByEmail("test@gmail.com").orElseThrow(UserNotFoundException::new);

        Study existedStudy = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        StudyManager manager = StudyManager.createManager(user, existedStudy);
        studyManagerRepository.save(manager);
        existedStudy.addManager(manager);
        Study savedStudy = studyRepository.save(existedStudy);

        Study study = Study.builder()
                .path(path2)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        StudyManager manager2 = StudyManager.createManager(user, study);
        studyManagerRepository.save(manager2);
        study.addManager(manager2);
        Study savedStudy2 = studyRepository.save(study);

        StudyPathUpdateRequest request = new StudyPathUpdateRequest(path);
        String json = objectMapper.writeValueAsString(request);


        // expected
        mockMvc.perform(MockMvcRequestBuilders.patch("/study/{path}/setting/path", path2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.errorCode")
                        .value(ErrorCode.STUDY_PATH_ALREADY_EXISTS.getErrorCode()))
                .andExpect(jsonPath("$.errorMessage")
                        .value(ErrorCode.STUDY_PATH_ALREADY_EXISTS.getMessage()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("스터디 title 수정 성공테스트")
    void update_study_title_correct_input() throws Exception {
        // given
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

        StudyTitleUpdateRequest request = new StudyTitleUpdateRequest("correct title");
        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.patch("/study/{path}/setting/title", path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("title").value("correct title"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("잘못된 경로로 스터디 title 수정 시도하면 예외 발생")
    void update_study_title_wrong_path() throws Exception {
        // given
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

        StudyTitleUpdateRequest request = new StudyTitleUpdateRequest("correct title");
        String json = objectMapper.writeValueAsString(request);


        // expected
        mockMvc.perform(MockMvcRequestBuilders.patch("/study/{path}/setting/title", "wrong")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.errorCode")
                        .value(ErrorCode.STUDY_NOT_EXISTS.getErrorCode()))
                .andExpect(jsonPath("$.errorMessage")
                        .value(ErrorCode.STUDY_NOT_EXISTS.getMessage()))
                .andDo(MockMvcResultHandlers.print());
    }
}