package com.app.domain.study.service;

import com.app.TestUtils;
import com.app.WithAccount;
import com.app.domain.common.dto.PagedResponse;
import com.app.domain.study.Study;
import com.app.domain.study.dto.*;
import com.app.domain.study.dto.studySetting.StudyPathUpdateRequest;
import com.app.domain.study.repository.StudyRepository;
import com.app.domain.study.studyManager.StudyManager;
import com.app.domain.study.studyManager.repository.StudyManagerRepository;
import com.app.domain.study.studyMember.StudyMember;
import com.app.domain.study.studyMember.repository.StudyMemberRepository;
import com.app.domain.user.User;
import com.app.domain.user.constant.Role;
import com.app.domain.user.repository.UserRepository;
import com.app.global.error.exception.*;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.app.TestUtils.getAuthenticatedEmail;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @Autowired
    StudyMemberRepository studyMemberRepository;

    @Test
    @WithAccount
    @DisplayName("스터디 path 검색 테스트")
    void findByPath() {
        // given
        String path = "test";
        User user = userRepository.findByEmail(getAuthenticatedEmail()).orElseThrow(UserNotFoundException::new);
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        Study savedStudy = studyRepository.save(study);

        // when
        Study foundStudy = studyService.findByPath(path);
        // then
        assertThat(foundStudy).extracting("path", "title", "shortDescription", "fullDescription")
                .containsExactly("test", "테스트 스터디1", "짧은 글 설명1", "긴 글 설명1");
    }

    @Test
    @WithAccount
    @DisplayName("매니저 권한으로 스터디 검색 테스트")
    void findAuthorizedStudy_by_manager() {
        // given
        String path = "test";
        User user = userRepository.findByEmail(getAuthenticatedEmail()).orElseThrow(UserNotFoundException::new);
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        Study savedStudy = studyRepository.save(study);

        StudyManager savedManager = studyManagerRepository.save(StudyManager.createManager(user, study));
        study.addManager(savedManager);

        // when
        Study foundStudy = studyService.findAuthorizedStudy(user.getId(), path);

        // then
        assertThat(foundStudy).extracting("path", "title", "shortDescription", "fullDescription")
                .containsExactly("test", "테스트 스터디1", "짧은 글 설명1", "긴 글 설명1");
    }

    @Test
    @WithAccount(name = "guest", email = "guest@gmail.com", role = "ROLE_GUEST")
    @DisplayName("권한 없이 매니저 권한 스터디 검색하면 예외 발생")
    void findAuthorizedStudy_without_manager_permissions() {
        // given
        String path = "test";
        User user = User.builder()
                .name("관리자")
                .email("admin@example.com")
                .role(Role.ADMIN)
                .build();
        User savedUser = userRepository.save(user);

        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        Study savedStudy = studyRepository.save(study);

        StudyManager savedManager = studyManagerRepository.save(StudyManager.createManager(savedUser, study));
        study.addManager(savedManager);

        User guest = userRepository.findByEmail("guest@gmail.com").orElseThrow(UserNotFoundException::new);

        // then
        assertThatThrownBy(() -> studyService.findAuthorizedStudy(guest.getId(), path)).isInstanceOf(UnauthorizedAccessException.class);
    }

    @Test
    @WithAccount
    @DisplayName("스터디 생성 테스트")
    void create_study_with_correct_input() {
        // given
        User user = userRepository.findByEmail(getAuthenticatedEmail()).orElseThrow(UserNotFoundException::new);

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
        User user = userRepository.findByEmail(getAuthenticatedEmail()).orElseThrow(UserNotFoundException::new);
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        Study savedStudy = studyRepository.save(study);

        StudyManager savedManager = studyManagerRepository.save(StudyManager.createManager(user, savedStudy));
        savedStudy.addManager(savedManager);

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
        User user = userRepository.findByEmail(getAuthenticatedEmail()).orElseThrow(UserNotFoundException::new);
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        Study savedStudy = studyRepository.save(study);

        StudyManager savedManager = studyManagerRepository.save(StudyManager.createManager(user, savedStudy));
        savedStudy.addManager(savedManager);

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
        User user = userRepository.findByEmail(getAuthenticatedEmail()).orElseThrow(UserNotFoundException::new);
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        Study savedStudy = studyRepository.save(study);

        StudyManager savedManager = studyManagerRepository.save(StudyManager.createManager(user, savedStudy));
        savedStudy.addManager(savedManager);

        // expected
        assertThatThrownBy(() -> studyService.getStudy(user.getId(), "wrong")).isInstanceOf(StudyNotFoundException.class);
    }

    @Test
    @WithAccount
    @DisplayName("전체 스터디 목록 조회 성공 테스트")
    void get_studies_with_correct_input() {
        // given
        User user = userRepository.findByEmail(getAuthenticatedEmail()).orElseThrow(UserNotFoundException::new);
        for (int i = 1; i <= 10; i++) {
            Study study = Study.builder()
                    .path("path" + i)
                    .title("테스트 스터디" + i)
                    .shortDescription("짧은 글 설명" + i)
                    .fullDescription("긴 글 설명" + i)
                    .build();
            studyRepository.save(study);

            StudyManager savedManager = studyManagerRepository.save(StudyManager.createManager(user, study));
            study.addManager(savedManager);
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

    @Test
    @WithAccount
    @DisplayName("관리 스터디 목록 조회 성공 테스트")
    void get_managed_studies_with_correct_input() {
        // given
        // 스터디 세팅
        User user = userRepository.findByEmail(getAuthenticatedEmail()).orElseThrow(UserNotFoundException::new);
        for (int i = 1; i <= 10; i++) {
            Study study = Study.builder()
                    .path("path" + i)
                    .title("테스트 스터디" + i)
                    .shortDescription("짧은 글 설명" + i)
                    .fullDescription("긴 글 설명" + i)
                    .build();
            studyRepository.save(study);

            StudyManager savedManager = studyManagerRepository.save(StudyManager.createManager(user, study));
            study.addManager(savedManager);
        }

        // when
        PagedResponse<StudyQueryResponse> response = studyService.getMyManagedStudies(user.getId(), PageRequest.of(0, 9));
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

    @Test
    @WithAccount(name = "guest", email = "guest@gmail.com", role = "ROLE_GUEST")
    @DisplayName("가입 스터디 목록 조회 성공 테스트")
    void get_joined_studies_with_correct_input() {
        // given
        // 스터디 세팅
        User user = User.builder()
                .name("testName")
                .email("test@gmail.com")
                .role(Role.ADMIN)
                .build();
        User savedUser = userRepository.save(user);
        for (int i = 1; i <= 10; i++) {
            Study study = Study.builder()
                    .path("path" + i)
                    .title("테스트 스터디" + i)
                    .shortDescription("짧은 글 설명" + i)
                    .fullDescription("긴 글 설명" + i)
                    .build();
            studyRepository.save(study);

            StudyManager savedManager = studyManagerRepository.save(StudyManager.createManager(savedUser, study));
            study.addManager(savedManager);
        }

        User guest = userRepository.findByEmail("guest@gmail.com").orElseThrow(UserNotFoundException::new);

        List<String> pathList = new ArrayList<>();
        for (int i = 3; i <= 7; i++) {
            pathList.add("path" + i);
        }
        List<Study> studies = studyRepository.findByPathIn(pathList);
        for (Study study : studies) {
            study.publish();
            StudyMember savedMember = studyMemberRepository.save(StudyMember.createMember(guest, study));
            study.addMember(savedMember);
        }

        // when
        PagedResponse<StudyQueryResponse> response =
                studyService.getMyJoinedStudies(guest.getId(), PageRequest.of(0, 9));

        // then
        assertThat(response.getCurrentPage()).isEqualTo(1);
        assertThat(response.getSize()).isEqualTo(9);
        assertThat(response.getTotalCount()).isEqualTo(5);
        assertThat(response.getTotalPages()).isEqualTo(1);
        assertThat(response.getContent()).extracting("path", "title", "shortDescription")
                .hasSize(5);
        assertThat(response.getContent().get(0))
                .extracting("path", "title", "shortDescription")
                .containsExactly("path3", "테스트 스터디3", "짧은 글 설명3");
        assertThat(response.getContent().get(4))
                .extracting("path", "title", "shortDescription")
                .containsExactly("path7", "테스트 스터디7", "짧은 글 설명7");
    }

    @Test
    @WithAccount(name = "guest", email = "guest@gmail.com", role = "ROLE_GUEST")
    @DisplayName("가입 스터디가 없을 경우 목록 조회 성공 테스트")
    void get_joined_studies_with_nothing() {
        // given
        // 스터디 세팅
        User user = User.builder()
                .name("testName")
                .email("test@gmail.com")
                .role(Role.ADMIN)
                .build();
        User savedUser = userRepository.save(user);
        for (int i = 1; i <= 10; i++) {
            Study study = Study.builder()
                    .path("path" + i)
                    .title("테스트 스터디" + i)
                    .shortDescription("짧은 글 설명" + i)
                    .fullDescription("긴 글 설명" + i)
                    .build();
            studyRepository.save(study);

            StudyManager savedManager = studyManagerRepository.save(StudyManager.createManager(savedUser, study));
            study.addManager(savedManager);
        }

        User guest = userRepository.findByEmail("guest@gmail.com").orElseThrow(UserNotFoundException::new);

        // when
        PagedResponse<StudyQueryResponse> response =
                studyService.getMyJoinedStudies(guest.getId(), PageRequest.of(0, 9));

        // then
        assertThat(response.getCurrentPage()).isEqualTo(1);
        assertThat(response.getSize()).isEqualTo(9);
        assertThat(response.getTotalCount()).isEqualTo(0);
        assertThat(response.getTotalPages()).isEqualTo(1);
        assertThat(response.getContent()).extracting("path", "title", "shortDescription")
                .hasSize(0);
    }

    @Test
    @WithAccount
    @DisplayName("스터디 소개 수정 테스트")
    void updateDescription() {
        // given
        String path = "test";
        User user = userRepository.findByEmail(getAuthenticatedEmail()).orElseThrow(UserNotFoundException::new);
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        Study savedStudy = studyRepository.save(study);

        StudyManager savedManager = studyManagerRepository.save(StudyManager.createManager(user, savedStudy));
        savedStudy.addManager(savedManager);

        StudyUpdateRequest request = StudyUpdateRequest.builder()
                .shortDescription("수정된 짧은 글 설명1")
                .fullDescription("수정된 긴 글 설명1")
                .build();

        // when
        StudyUpdateResponse response = studyService.updateDescription(user.getId(), path, request);

        // then
        assertThat(response).extracting("path", "title", "shortDescription", "fullDescription")
                .containsExactly("test", "테스트 스터디1", "수정된 짧은 글 설명1", "수정된 긴 글 설명1");
    }

    @Test
    @WithAccount
    @DisplayName("스터디를 매니저 정보와 함께 반환")
    void findStudyWithManager() {
        // given
        String path = "test";
        User user = userRepository.findByEmail(getAuthenticatedEmail()).orElseThrow(UserNotFoundException::new);
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        Study savedStudy = studyRepository.save(study);

        StudyManager savedManager = studyManagerRepository.save(StudyManager.createManager(user, savedStudy));
        savedStudy.addManager(savedManager);

        // when
        Study foundStudy = studyService.findStudyWithManager(user.getId(), path);

        // then
        assertThat(foundStudy).extracting("path", "title", "shortDescription", "fullDescription")
                .containsExactly("test", "테스트 스터디1", "짧은 글 설명1", "긴 글 설명1");
        assertThat(foundStudy.getStudyManagers().size()).isEqualTo(1);
        assertThat(foundStudy.getStudyManagers().contains(savedManager)).isTrue();
    }

    @Test
    @WithAccount(name = "guest", email = "guest@gmail.com", role = "ROLE_GUEST")
    @DisplayName("스터디 가입 성공 테스트")
    void joinStudy() {
        // given
        String path = "test";
        User user = User.builder()
                .name("testName")
                .email("test@gmail.com")
                .role(Role.ADMIN)
                .build();
        User savedUser = userRepository.save(user);
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        Study savedStudy = studyRepository.save(study);

        StudyManager savedManager = studyManagerRepository.save(StudyManager.createManager(savedUser, savedStudy));
        savedStudy.addManager(savedManager);
        savedStudy.publish();

        User guest = userRepository.findByEmail(getAuthenticatedEmail()).orElseThrow(UserNotFoundException::new);

        // when
        StudyResponse response = studyService.joinStudy(guest.getId(), path);

        // then
        assertThat(response.getMembers()).hasSize(1)
                .extracting("name", "email").containsExactlyInAnyOrder(Tuple.tuple("guest", "guest@gmail.com"));
    }

    @Test
    @WithAccount(name = "guest", email = "guest@gmail.com", role = "ROLE_GUEST")
    @DisplayName("스터디 publish 전 가입 시도하면 예외 발생")
    void joinStudy_before_study_publish() {
        // given
        String path = "test";
        User user = User.builder()
                .name("testName")
                .email("test@gmail.com")
                .role(Role.ADMIN)
                .build();
        User savedUser = userRepository.save(user);
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        Study savedStudy = studyRepository.save(study);

        StudyManager savedManager = studyManagerRepository.save(StudyManager.createManager(savedUser, savedStudy));
        savedStudy.addManager(savedManager);

        User guest = userRepository.findByEmail(getAuthenticatedEmail()).orElseThrow(UserNotFoundException::new);

        // expected
        assertThatThrownBy(() -> studyService.joinStudy(guest.getId(), path)).isInstanceOf(InvalidStudyJoinConditionException.class);
    }

    @Test
    @WithAccount(name = "guest", email = "guest@gmail.com", role = "ROLE_GUEST")
    @DisplayName("이미 스터디 가입한 멤버가 가입 시도하면 예외 발생")
    void joinStudy_already_study_joined() {
        // given
        String path = "test";
        User user = User.builder()
                .name("testName")
                .email("test@gmail.com")
                .role(Role.ADMIN)
                .build();
        User savedUser = userRepository.save(user);
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        Study savedStudy = studyRepository.save(study);

        StudyManager savedManager = studyManagerRepository.save(StudyManager.createManager(savedUser, savedStudy));
        savedStudy.addManager(savedManager);
        savedStudy.publish();

        User guest = userRepository.findByEmail(getAuthenticatedEmail()).orElseThrow(UserNotFoundException::new);
        StudyMember savedMember = studyMemberRepository.save(StudyMember.createMember(guest, savedStudy));
        savedStudy.addMember(savedMember);

        // expected
        assertThatThrownBy(() -> studyService.joinStudy(guest.getId(), path)).isInstanceOf(InvalidStudyJoinConditionException.class);
    }

    @Test
    @WithAccount
    @DisplayName("스터디 매니저가 가입 시도하면 예외 발생")
    void joinStudy_by_manager() {
        // given
        String path = "test";
        User savedUser = userRepository.findByEmail(getAuthenticatedEmail()).orElseThrow(UserNotFoundException::new);
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        Study savedStudy = studyRepository.save(study);

        StudyManager savedManager = studyManagerRepository.save(StudyManager.createManager(savedUser, savedStudy));
        savedStudy.addManager(savedManager);
        savedStudy.publish();

        // expected
        assertThatThrownBy(() -> studyService.joinStudy(savedUser.getId(), path)).isInstanceOf(InvalidStudyJoinConditionException.class);
    }

    @Test
    @WithAccount(name = "guest", email = "guest@gmail.com", role = "ROLE_GUEST")
    @DisplayName("스터디 탈퇴 성공 테스트")
    void leaveStudy() {
        // given
        String path = "test";
        User user = User.builder()
                .name("testName")
                .email("test@gmail.com")
                .role(Role.ADMIN)
                .build();
        User savedUser = userRepository.save(user);

        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        Study savedStudy = studyRepository.save(study);

        StudyManager manager = StudyManager.createManager(savedUser, savedStudy);
        StudyManager savedManager = studyManagerRepository.save(manager);
        savedStudy.addManager(savedManager);
        savedStudy.publish();

        User guest = userRepository.findByEmail(getAuthenticatedEmail()).orElseThrow(UserNotFoundException::new);
        StudyMember savedMember = studyMemberRepository.save(StudyMember.createMember(guest, savedStudy));
        savedStudy.addMember(savedMember);

        // when
        assertThat(savedStudy.getStudyMembers()).hasSize(1); // 체크용
        StudyResponse response = studyService.leaveStudy(guest.getId(), path);

        // then
        assertThat(response.getMembers()).hasSize(0);
        assertThat(savedStudy.getStudyMembers()).isEmpty();
        assertThat(savedStudy.getMemberCount()).isEqualTo(0);
    }

    @Test
    @WithAccount
    @DisplayName("스터디 publish 성공 테스트")
    void publishStudy() {
        // given
        String path = "test";
        User savedUser = userRepository.findByEmail(getAuthenticatedEmail()).orElseThrow(UserNotFoundException::new);
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        Study savedStudy = studyRepository.save(study);

        StudyManager savedManager = studyManagerRepository.save(StudyManager.createManager(savedUser, savedStudy));
        savedStudy.addManager(savedManager);

        // when
        assertThat(savedStudy.isPublished()).isFalse();
        Boolean published = studyService.publishStudy(savedUser.getId(), path);

        // then
        Assertions.assertThat(published).isTrue();
        assertThat(savedStudy.isPublished()).isTrue();
    }

    @Test
    @WithAccount(name = "guest", email = "guest@gmail.com", role = "ROLE_GUEST")
    @DisplayName("권한없이 스터디 publish 시도하면 예외 발생")
    void publishStudy_without_manager_permissions() {
        // given
        String path = "test";
        User user = User.builder()
                .name("testName")
                .email("test@gmail.com")
                .role(Role.ADMIN)
                .build();
        User savedUser = userRepository.save(user);
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        Study savedStudy = studyRepository.save(study);

        StudyManager savedManager = studyManagerRepository.save(StudyManager.createManager(savedUser, savedStudy));
        savedStudy.addManager(savedManager);

        User guest = userRepository.findByEmail(getAuthenticatedEmail()).orElseThrow(UserNotFoundException::new);

        // expected
        assertThatThrownBy(() -> studyService.publishStudy(guest.getId(), path)).isInstanceOf(UnauthorizedAccessException.class);
    }

    @Test
    @WithAccount
    @DisplayName("스터디 모집 시작 성공 테스트")
    void startRecruit() {
        // given
        String path = "test";
        User savedUser = userRepository.findByEmail(getAuthenticatedEmail()).orElseThrow(UserNotFoundException::new);
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        Study savedStudy = studyRepository.save(study);

        StudyManager savedManager = studyManagerRepository.save(StudyManager.createManager(savedUser, savedStudy));
        savedStudy.addManager(savedManager);
        savedStudy.publish();

        // when
        assertThat(savedStudy.isRecruiting()).isFalse();
        Boolean isRecruiting = studyService.startRecruit(savedUser.getId(), path);

        // then
        Assertions.assertThat(isRecruiting).isTrue();
        assertThat(savedStudy.isRecruiting()).isTrue();
    }

    @Test
    @WithAccount
    @DisplayName("스터디 모집 종료 성공 테스트")
    void stopRecruit() {
        // given
        String path = "test";
        User savedUser = userRepository.findByEmail(getAuthenticatedEmail()).orElseThrow(UserNotFoundException::new);
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        Study savedStudy = studyRepository.save(study);

        StudyManager savedManager = studyManagerRepository.save(StudyManager.createManager(savedUser, savedStudy));
        savedStudy.addManager(savedManager);
        savedStudy.publish();
        Clock offsetClock = Clock.offset(Clock.systemDefaultZone(), Duration.ofMinutes(-60));
        savedStudy.startRecruit(LocalDateTime.now(offsetClock));

        // when
        assertThat(savedStudy.isRecruiting()).isTrue();
        Boolean isRecruiting = studyService.stopRecruit(savedUser.getId(), path);

        // then
        Assertions.assertThat(isRecruiting).isFalse();
        assertThat(savedStudy.isRecruiting()).isFalse();
    }

    @Test
    @WithAccount
    @DisplayName("스터디 경로 수정 성공 테스트")
    void updateStudyPath() {
        // given
        String path = "test";
        User savedUser = userRepository.findByEmail(getAuthenticatedEmail()).orElseThrow(UserNotFoundException::new);
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        Study savedStudy = studyRepository.save(study);

        StudyManager savedManager = studyManagerRepository.save(StudyManager.createManager(savedUser, savedStudy));
        savedStudy.addManager(savedManager);
        savedStudy.publish();

        StudyPathUpdateRequest request = new StudyPathUpdateRequest("newPath");

        // when
        String newPath = studyService.updateStudyPath(savedUser.getId(), path, request);

        // then
        Assertions.assertThat(newPath).isEqualTo("newPath");
    }

    @Test
    @WithAccount
    @DisplayName("이미 존재하는 경로로 경로 수정시 예외 발생")
    void updateStudyPath_with_already_exist_path() {
        // given
        String path = "path";
        User savedUser = userRepository.findByEmail(getAuthenticatedEmail()).orElseThrow(UserNotFoundException::new);
        for (int i = 1; i <= 2; i++) {
            Study study = Study.builder()
                    .path(path + i)
                    .title("테스트 스터디" + i)
                    .shortDescription("짧은 글 설명" + i)
                    .fullDescription("긴 글 설명" + i)
                    .build();
            Study savedStudy = studyRepository.save(study);

            StudyManager savedManager = studyManagerRepository.save(StudyManager.createManager(savedUser, savedStudy));
            savedStudy.addManager(savedManager);
            savedStudy.publish();
        }

        StudyPathUpdateRequest request = new StudyPathUpdateRequest("path2");

        // expected
        assertThatThrownBy(() -> studyService.updateStudyPath(savedUser.getId(), "path1", request))
                .isInstanceOf(StudyPathAlreadyExistException.class);
    }
}