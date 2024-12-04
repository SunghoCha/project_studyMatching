package com.app.domain.study.service;

import com.app.WithAccount;
import com.app.domain.common.dto.PagedResponse;
import com.app.domain.study.Study;
import com.app.domain.study.dto.StudyCreateRequest;
import com.app.domain.study.dto.StudyCreateResponse;
import com.app.domain.study.dto.StudyQueryResponse;
import com.app.domain.study.dto.StudyResponse;
import com.app.domain.study.repository.StudyRepository;
import com.app.domain.study.studyManager.StudyManager;
import com.app.domain.study.studyManager.repository.StudyManagerRepository;
import com.app.domain.user.User;
import com.app.domain.user.repository.UserRepository;
import com.app.global.error.exception.StudyNotFoundException;
import com.app.global.error.exception.StudyPathAlreadyExistException;
import com.app.global.error.exception.UserNotFoundException;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class StudyServiceTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    StudyService studyService;

    @Autowired
    StudyRepository studyRepository;

    @Autowired
    StudyManagerRepository studyManagerRepository;

    @Test
    @WithAccount
    @DisplayName("스터디 생성 테스트")
    void create_study_with_correct_input() {
        // given
        User user = userRepository.findByEmail("test@gmail.com").orElseThrow(UserNotFoundException::new);

        StudyCreateRequest request = StudyCreateRequest.builder()
                .path("path1")
                .title("title1")
                .shortDescription("short1")
                .build();

        // when
        StudyCreateResponse response = studyService.createStudy(user.getId(), request);

        // then
        assertThat(response).extracting("path", "title", "shortDescription")
                .containsExactlyInAnyOrder("path1", "title1", "short1");
    }

    @Test
    @WithAccount
    @DisplayName("이미 생성된 스터디와 같은 path 입력시 예외 발생")
    void create_study_with_wrong_path() {
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
        StudyManager studyManager = studyManagerRepository.save(manager);
        savedStudy.addManager(studyManager);

        StudyCreateRequest request = StudyCreateRequest.builder()
                .path(path)
                .title("title1")
                .shortDescription("short1")
                .build();

        // expected
        assertThatThrownBy(() -> studyService.createStudy(user.getId(), request)).isInstanceOf(StudyPathAlreadyExistException.class);
    }

    @Test
    @WithAccount
    @DisplayName("스터디 조회 성공 테스트")
    void get_study_with_correct_input() {
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
        StudyManager studyManager = studyManagerRepository.save(manager);
        savedStudy.addManager(studyManager);

        StudyResponse response = studyService.getStudy(user.getId(), path);

        // expected
        assertThat(response).extracting("path", "title", "shortDescription", "fullDescription")
                .containsExactly(path, "테스트 스터디1", "짧은 글 설명1", "긴 글 설명1");
    }

    @Test
    @WithAccount
    @DisplayName("잘못된 path로 스터디 조회시 예외 발생")
    void get_study_with_wrong_path() {
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
        StudyManager studyManager = studyManagerRepository.save(manager);
        savedStudy.addManager(studyManager);

        // expected
        assertThatThrownBy(() -> studyService.getStudy(user.getId(), "wrong")).isInstanceOf(StudyNotFoundException.class);
    }

        @Test
        @WithAccount
        @DisplayName("전체 스터디 목록 조회 성공 테스트")
        void get_studies_with_correct_input() {
            // given
            User user = userRepository.findByEmail("test@gmail.com").orElseThrow(UserNotFoundException::new);
            for (int i = 1; i <= 10; i++ ) {
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
            // when
            PagedResponse<StudyQueryResponse> response = studyService.getStudies(null, PageRequest.of(0, 9));
            // then
            assertThat(response.getCurrentPage()).isEqualTo(1);
            assertThat(response.getSize()).isEqualTo(9);
            assertThat(response.getTotalCount()).isEqualTo(10);
            assertThat(response.getTotalPages()).isEqualTo(2);
            assertThat(response.getContent()).extracting("path", "title", "shortDescription")
                    .hasSize(9);
            assertThat(response.getContent().get(0))
                    .extracting("path", "title", "shortDescription")
                    .containsExactly("path1", "테스트 스터디1", "짧은 글 설명1");
            assertThat(response.getContent().get(8))
                    .extracting("path", "title", "shortDescription")
                    .containsExactly("path9", "테스트 스터디9", "짧은 글 설명9");
        }
}