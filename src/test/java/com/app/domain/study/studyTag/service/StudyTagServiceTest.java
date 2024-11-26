package com.app.domain.study.studyTag.service;

import com.app.WithAccount;
import com.app.domain.study.Study;
import com.app.domain.study.repository.StudyRepository;
import com.app.domain.study.studyManager.StudyManager;
import com.app.domain.study.studyManager.repository.StudyManagerRepository;
import com.app.domain.study.studyTag.dto.StudyTagCreateRequest;
import com.app.domain.study.studyTag.dto.StudyTagCreateResponse;
import com.app.domain.tag.service.TagService;
import com.app.domain.user.User;
import com.app.domain.user.repository.UserRepository;
import com.app.global.error.exception.InvalidTagException;
import com.app.global.error.exception.UnauthorizedAccessException;
import com.app.global.error.exception.UserNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class StudyTagServiceTest {

    @Autowired
    TagService tagService;

    @Autowired
    StudyTagService studyTagService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    StudyRepository studyRepository;

    @Autowired
    StudyManagerRepository studyManagerRepository;

    @BeforeEach
    void setup() {
        tagService.initTagData();
    }

    @Test
    @WithAccount
    @DisplayName("StudyTag 추가 성공 테스트")
    void add_studyTags_with_correct_input() {
        // given
        String path = "path1";

        User user = userRepository.findByEmail("test@gmail.com").orElseThrow(UserNotFoundException::new);
        Study study = Study.builder()
                .path(path)
                .title("title1")
                .shortDescription("short1")
                .fullDescription("full1")
                .build();
        StudyManager manager = StudyManager.createManager(user, study);
        studyManagerRepository.save(manager);
        study.addManager(manager);
        studyRepository.save(study);

        StudyTagCreateRequest request = StudyTagCreateRequest.builder()
                .tags(Set.of("Spring", "Java"))
                .build();
        // when
        StudyTagCreateResponse response = studyTagService.createStudyTags(user.getId(), path, request);
        // then
        assertThat(response.getTags()).hasSize(2)
                .containsExactlyInAnyOrder("Spring", "Java");

    }

    @Test
    @WithAccount
    @DisplayName("잘못된 StudyTag 추가 요청하면 예외 발생")
    void add_studyTags_with_wrong_input() {
        // given
        String path = "path1";

        User user = userRepository.findByEmail("test@gmail.com").orElseThrow(UserNotFoundException::new);
        Study study = Study.builder()
                .path(path)
                .title("title1")
                .shortDescription("short1")
                .fullDescription("full1")
                .build();
        StudyManager manager = StudyManager.createManager(user, study);
        studyManagerRepository.save(manager);
        study.addManager(manager);
        studyRepository.save(study);

        StudyTagCreateRequest request = StudyTagCreateRequest.builder()
                .tags(Set.of("Spring2", "Java2"))
                .build();

        // expected
        assertThatThrownBy(() -> studyTagService.createStudyTags(user.getId(), path, request))
                .isInstanceOf(InvalidTagException.class);

    }

    @Test
    @WithAccount
    @DisplayName("수정권한없이 studyTag 추가 요청하면 예외 발생")
    void add_studyTags_without_authorization() {
        // given
        String path = "path1";

        User user = userRepository.findByEmail("test@gmail.com").orElseThrow(UserNotFoundException::new);
        Study study = Study.builder()
                .path(path)
                .title("title1")
                .shortDescription("short1")
                .fullDescription("full1")
                .build();

        studyRepository.save(study);

        StudyTagCreateRequest request = StudyTagCreateRequest.builder()
                .tags(Set.of("Spring", "Java"))
                .build();

        // expected
        assertThatThrownBy(() -> studyTagService.createStudyTags(user.getId(), path, request))
                .isInstanceOf(UnauthorizedAccessException.class);
    }
}