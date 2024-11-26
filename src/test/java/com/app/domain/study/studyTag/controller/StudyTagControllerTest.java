package com.app.domain.study.studyTag.controller;

import com.app.TestUtils;
import com.app.WithAccount;
import com.app.domain.study.Study;
import com.app.domain.study.repository.StudyRepository;
import com.app.domain.study.studyManager.StudyManager;
import com.app.domain.study.studyManager.repository.StudyManagerRepository;
import com.app.domain.study.studyTag.StudyTag;
import com.app.domain.study.studyTag.dto.StudyTagCreateRequest;
import com.app.domain.study.studyTag.dto.StudyTagUpdateRequest;
import com.app.domain.study.studyTag.repository.StudyTagRepository;
import com.app.domain.tag.Tag;
import com.app.domain.tag.repository.TagRepository;
import com.app.domain.user.User;
import com.app.domain.user.repository.UserRepository;
import com.app.global.error.ErrorCode;
import com.app.global.error.exception.UserNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class StudyTagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    StudyRepository studyRepository;

    @Autowired
    StudyManagerRepository studyManagerRepository;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    StudyTagRepository studyTagRepository;


    @Test
    @WithAccount
    @DisplayName("StudyTag 생성 성공 테스트")
    void create_studyTag_with_correct_input() throws Exception {
        // given
        String path = "test";
        User user = userRepository.findByEmail("test@gmail.com").orElseThrow(UserNotFoundException::new);
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        Study savedStudy = studyRepository.save(study);

        StudyManager manager = StudyManager.createManager(user, savedStudy);
        studyManagerRepository.save(manager);
        study.addManager(manager);

        List<Tag> tags = TestUtils.createTags(5);
        Set<String> tagTitles = tagRepository.saveAll(tags).stream()
                .map(Tag::getTitle)
                .collect(Collectors.toSet());

        StudyTagCreateRequest request = StudyTagCreateRequest.builder()
                .tags(tagTitles)
                .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/study-tag/{path}", path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.tags").value(Matchers.containsInAnyOrder(
                        "tag1", "tag2", "tag3", "tag4", "tag5"
                )))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("잘못된 tag로 studyTag 등록 요청보내면 예외 발생")
    void create_studyTag_with_wrong_tag() throws Exception {
        // given
        String path = "test";
        User user = userRepository.findByEmail("test@gmail.com").orElseThrow(UserNotFoundException::new);
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        Study savedStudy = studyRepository.save(study);

        StudyManager manager = StudyManager.createManager(user, savedStudy);
        studyManagerRepository.save(manager);
        study.addManager(manager);

        List<Tag> tags = TestUtils.createTags(5);
        Set<String> tagTitles = tagRepository.saveAll(tags).stream()
                .map(Tag::getTitle)
                .collect(Collectors.toSet());

        StudyTagCreateRequest request = StudyTagCreateRequest.builder()
                .tags(Set.of("tag123", "tag4567"))
                .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/study-tag/{path}", path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value(ErrorCode.INVALID_TAG.getErrorCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage")
                        .value(ErrorCode.INVALID_TAG.getMessage()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("studyTag 조회 성공 테스트")
    void get_studyTag_with_correct_input() throws Exception {
        // given
        // study 생성
        String path = "test";
        User user = userRepository.findByEmail("test@gmail.com").orElseThrow(UserNotFoundException::new);
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        Study savedStudy = studyRepository.save(study);

        StudyManager manager = StudyManager.createManager(user, savedStudy);
        studyManagerRepository.save(manager);
        study.addManager(manager);

        // studyTag 생성
        List<Tag> tags = TestUtils.createTags(5);
        List<Tag> savedTags = tagRepository.saveAll(tags);

        Set<StudyTag> studyTags = savedTags.stream()
                .map(tag -> StudyTag.builder()
                        .study(study)
                        .tag(tag)
                        .build())
                .collect(Collectors.toSet());

        List<StudyTag> savedStudyTags = studyTagRepository.saveAll(studyTags);
        study.addStudyTags(new HashSet<>(savedStudyTags));

        // expected
        mockMvc.perform(MockMvcRequestBuilders.get("/study-tag/{path}", path))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.tags")
                        .value(Matchers.containsInAnyOrder("tag1", "tag2", "tag3", "tag4", "tag5")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("잘못된 path로 studyTag 조회시 예외 발생")
    void get_studyTag_with_wrong_path() throws Exception {
        // given
        // study 생성
        String path = "test";
        User user = userRepository.findByEmail("test@gmail.com").orElseThrow(UserNotFoundException::new);
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        Study savedStudy = studyRepository.save(study);

        StudyManager manager = StudyManager.createManager(user, savedStudy);
        studyManagerRepository.save(manager);
        study.addManager(manager);

        // studyTag 생성
        List<Tag> tags = TestUtils.createTags(5);
        List<Tag> savedTags = tagRepository.saveAll(tags);

        Set<StudyTag> studyTags = savedTags.stream()
                .map(tag -> StudyTag.builder()
                        .study(study)
                        .tag(tag)
                        .build())
                .collect(Collectors.toSet());

        List<StudyTag> savedStudyTags = studyTagRepository.saveAll(studyTags);
        study.addStudyTags(new HashSet<>(savedStudyTags));

        // expected
        mockMvc.perform(MockMvcRequestBuilders.get("/study-tag/{path}", "wrong"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errorCode")
                        .value(ErrorCode.STUDY_NOT_EXISTS.getErrorCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("errorMessage")
                        .value(ErrorCode.STUDY_NOT_EXISTS.getMessage()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("studyTag 수정 성공 테스트")
    void update_studyTag_with_correct_input() throws Exception {
        // given
        // study 생성
        String path = "test";
        User user = userRepository.findByEmail("test@gmail.com").orElseThrow(UserNotFoundException::new);
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        Study savedStudy = studyRepository.save(study);

        StudyManager manager = StudyManager.createManager(user, savedStudy);
        studyManagerRepository.save(manager);
        study.addManager(manager);

        // studyTag 생성
        List<Tag> tags = TestUtils.createTags(10);
        List<Tag> savedTags = tagRepository.saveAll(tags);

        int middle = savedTags.size() / 2;

        List<Tag> oldTags = savedTags.subList(0, middle);
        List<Tag> newTags = savedTags.subList(2, middle + 1);

        Set<StudyTag> studyTags = oldTags.stream()
                .map(tag -> StudyTag.builder()
                        .study(study)
                        .tag(tag)
                        .build())
                .collect(Collectors.toSet());

        List<StudyTag> savedStudyTags = studyTagRepository.saveAll(studyTags);
        study.addStudyTags(new HashSet<>(savedStudyTags));

        StudyTagUpdateRequest request = StudyTagUpdateRequest.builder()
                .tags(newTags.stream()
                        .map(Tag::getTitle)
                        .collect(Collectors.toSet()))
                .build();
        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.patch("/study-tag/{path}", path).
                        contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.tags")
                        .value(Matchers.containsInAnyOrder("tag3", "tag4", "tag5", "tag6")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("잘못된 path로 studyTag 수정 요청하면 예외 발생")
    void update_studyTag_with_wrong_path() throws Exception {
        // given
        // study 생성
        String path = "test";
        User user = userRepository.findByEmail("test@gmail.com").orElseThrow(UserNotFoundException::new);
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        Study savedStudy = studyRepository.save(study);

        StudyManager manager = StudyManager.createManager(user, savedStudy);
        studyManagerRepository.save(manager);
        study.addManager(manager);

        // studyTag 생성
        List<Tag> tags = TestUtils.createTags(10);
        List<Tag> savedTags = tagRepository.saveAll(tags);

        int middle = savedTags.size() / 2;

        List<Tag> oldTags = savedTags.subList(0, middle);
        List<Tag> newTags = savedTags.subList(2, middle + 1);

        Set<StudyTag> studyTags = oldTags.stream()
                .map(tag -> StudyTag.builder()
                        .study(study)
                        .tag(tag)
                        .build())
                .collect(Collectors.toSet());

        List<StudyTag> savedStudyTags = studyTagRepository.saveAll(studyTags);
        study.addStudyTags(new HashSet<>(savedStudyTags));

        StudyTagUpdateRequest request = StudyTagUpdateRequest.builder()
                .tags(newTags.stream()
                        .map(Tag::getTitle)
                        .collect(Collectors.toSet()))
                .build();
        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.patch("/study-tag/{path}", "wrong").
                        contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value(ErrorCode.STUDY_NOT_EXISTS.getErrorCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage")
                        .value(ErrorCode.STUDY_NOT_EXISTS.getMessage()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("잘못된 tag로 studyTag 수정 요청하면 예외 발생")
    void update_studyTag_with_wrong_tag() throws Exception {
        // given
        // study 생성
        String path = "test";
        User user = userRepository.findByEmail("test@gmail.com").orElseThrow(UserNotFoundException::new);
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        Study savedStudy = studyRepository.save(study);

        StudyManager manager = StudyManager.createManager(user, savedStudy);
        studyManagerRepository.save(manager);
        study.addManager(manager);

        // studyTag 생성
        List<Tag> tags = TestUtils.createTags(10);
        List<Tag> savedTags = tagRepository.saveAll(tags);

        int middle = savedTags.size() / 2;

        List<Tag> oldTags = savedTags.subList(0, middle);

        Set<StudyTag> studyTags = oldTags.stream()
                .map(tag -> StudyTag.builder()
                        .study(study)
                        .tag(tag)
                        .build())
                .collect(Collectors.toSet());

        List<StudyTag> savedStudyTags = studyTagRepository.saveAll(studyTags);
        study.addStudyTags(new HashSet<>(savedStudyTags));

        StudyTagUpdateRequest request = StudyTagUpdateRequest.builder()
                .tags(Set.of("tag12345", "tag67890"))
                .build();
        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.patch("/study-tag/{path}", path).
                        contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value(ErrorCode.INVALID_TAG.getErrorCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage")
                        .value(ErrorCode.INVALID_TAG.getMessage()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("studyTag 삭제 성공 테스트")
    void delete_studyTag_with_correct_input() throws Exception {
        // given
        // study 생성
        String path = "test";
        User user = userRepository.findByEmail("test@gmail.com").orElseThrow(UserNotFoundException::new);
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        Study savedStudy = studyRepository.save(study);

        StudyManager manager = StudyManager.createManager(user, savedStudy);
        studyManagerRepository.save(manager);
        study.addManager(manager);

        // studyTag 생성
        List<Tag> tags = TestUtils.createTags(10);
        List<Tag> savedTags = tagRepository.saveAll(tags);

        Set<StudyTag> studyTags = savedTags.stream()
                .map(tag -> StudyTag.builder()
                        .study(study)
                        .tag(tag)
                        .build())
                .collect(Collectors.toSet());

        List<StudyTag> savedStudyTags = studyTagRepository.saveAll(studyTags);
        study.addStudyTags(new HashSet<>(savedStudyTags));

        // expected
        mockMvc.perform(MockMvcRequestBuilders.delete("/study-tag/{path}", path))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(10))
                .andDo(MockMvcResultHandlers.print());
    }
}