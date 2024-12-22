package com.app.domain.study;

import com.app.domain.study.studyManager.StudyManager;
import com.app.domain.study.studyMember.StudyMember;
import com.app.domain.study.studyTag.StudyTag;
import com.app.domain.study.studyZone.StudyZone;
import com.app.domain.user.User;
import com.app.domain.user.constant.Role;
import com.app.global.error.exception.InvalidRecruitmentStateException;
import com.app.global.error.exception.InvalidStudyJoinConditionException;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.app.TestUtils.createTags;
import static com.app.TestUtils.createZones;

class StudyTest {

    @Test
    @DisplayName("스터디를 공개하면 publish 상태를 true로 변경")
    void publish() {
        // given
        Study study = Study.builder()
                .path("test")
                .title("제목")
                .build();

        // when
        Assertions.assertThat(study.isPublished()).isFalse();
        study.publish(Clock.systemDefaultZone());

        // then
        Assertions.assertThat(study.isPublished()).isTrue();
    }

    @Test
    @DisplayName("스터디를 종료하면 close 상태를 true로 변경")
    void close() {
        // given
        Study study = Study.builder()
                .path("test")
                .title("제목")
                .build();

        // when
        Assertions.assertThat(study.isClosed()).isFalse();
        study.publish(Clock.systemDefaultZone());
        study.close(Clock.systemDefaultZone());

        // then
        Assertions.assertThat(study.isClosed()).isTrue();
    }

    @Test
    @DisplayName("studyManager를 인자로 전달하여 study의 멤버에 추가")
    void addManager() {
        // given
        User user = User.builder()
                .name("이름1")
                .email("이메일1")
                .role(Role.GUEST)
                .build();

        Study study = Study.builder()
                .title("제목")
                .path("path")
                .build();

        StudyManager manager = StudyManager.createManager(user, study);

        // when
        study.addManager(manager);

        // then
        Assertions.assertThat(study.getStudyManagers()).hasSize(1)
                .extracting("user.name", "study.title")
                .containsExactlyInAnyOrder(Tuple.tuple("이름1", "제목"));
    }

    @Test
    @DisplayName("studyMember를 인자로 전달하여 study의 멤버에 추가")
    void addMember() {
        // given
        User user = User.builder()
                .name("이름1")
                .email("이메일1")
                .role(Role.GUEST)
                .build();

        User guest = User.builder()
                .name("게스트1")
                .email("게스트이메일1")
                .role(Role.GUEST)
                .build();

        Study study = Study.builder()
                .title("제목")
                .path("path")
                .build();
        study.publish(Clock.systemDefaultZone());

        StudyMember member = StudyMember.createMember(guest, study);

        // when
        study.addMember(member);

        // then
        Assertions.assertThat(study.getStudyMembers()).hasSize(1)
                .extracting("user.name", "study.title")
                .containsExactlyInAnyOrder(Tuple.tuple("게스트1", "제목"));
    }

    @Test
    @DisplayName("스터디 관리자는 멤버로 등록할 수 없다.")
    void addMember_with_wrong_member() {
        // given
        User user = User.builder()
                .name("이름1")
                .email("이메일1")
                .role(Role.GUEST)
                .build();

        Study study = Study.builder()
                .title("제목")
                .path("path")
                .build();
        study.publish(Clock.systemDefaultZone());
        StudyManager manager = StudyManager.createManager(user, study);
        study.addManager(manager);

        StudyMember member = StudyMember.createMember(user, study);

        // expected
        Assertions.assertThatThrownBy(() -> study.addMember(member)).isInstanceOf(InvalidStudyJoinConditionException.class);
    }

    @Test
    @DisplayName("studyMember를 인자로 전달하여 study의 멤버에서 삭제")
    void removeMember() {
        // given
        User user = User.builder()
                .name("이름1")
                .email("이메일1")
                .role(Role.GUEST)
                .build();

        User guest = User.builder()
                .name("게스트1")
                .email("게스트이메일1")
                .role(Role.GUEST)
                .build();

        Study study = Study.builder()
                .title("제목")
                .path("path")
                .build();
        study.publish(Clock.systemDefaultZone());

        StudyMember member = StudyMember.createMember(guest, study);
        study.addMember(member);

        // when
        study.removeMember(member);

        // then
        Assertions.assertThat(study.getStudyMembers()).hasSize(0);
    }

    @Test
    @DisplayName("유저가 스터디의 멤버이면 true 반환")
    void isMember() {
        // given
        User user = User.builder()
                .name("이름1")
                .email("이메일1")
                .role(Role.GUEST)
                .build();

        User guest = User.builder()
                .name("게스트1")
                .email("게스트이메일1")
                .role(Role.GUEST)
                .build();

        Study study = Study.builder()
                .title("제목")
                .path("path")
                .build();
        study.publish(Clock.systemDefaultZone());

        StudyMember member = StudyMember.createMember(guest, study);
        study.addMember(member);

        // when
        boolean isMember = study.isMember(guest);
        boolean isMember2 = study.isMember(user);

        // then
        Assertions.assertThat(isMember).isTrue();
        Assertions.assertThat(isMember2).isFalse();
    }

    @Test
    @DisplayName("유저가 스터디의 매니저이면 true 반환")
    void isManager() {
        // given
        User user = User.builder()
                .name("이름1")
                .email("이메일1")
                .role(Role.GUEST)
                .build();

        User guest = User.builder()
                .name("게스트1")
                .email("게스트이메일1")
                .role(Role.GUEST)
                .build();

        Study study = Study.builder()
                .title("제목")
                .path("path")
                .build();
        study.publish(Clock.systemDefaultZone());
        StudyManager manager = StudyManager.createManager(user, study);
        study.addManager(manager);

        // when
        boolean isManager = study.isManager(user);
        boolean isManager2 = study.isManager(guest);

        // then
        Assertions.assertThat(isManager).isTrue();
        Assertions.assertThat(isManager2).isFalse();
    }

    @Test
    @DisplayName("스터디 공개 상태이고 유저가 이미 가입된 멤버이거나 매니저가 아니면 true 반환")
    void isJoinable() {
        // given
        User user = User.builder()
                .name("이름1")
                .email("이메일1")
                .role(Role.GUEST)
                .build();

        User guest = User.builder()
                .name("게스트1")
                .email("게스트이메일1")
                .role(Role.GUEST)
                .build();

        Study study = Study.builder()
                .title("제목")
                .path("path")
                .build();
        study.publish(Clock.systemDefaultZone());
        StudyManager manager = StudyManager.createManager(user, study);
        study.addManager(manager);

        // when
        boolean joinable = study.isJoinable(guest);

        // then
        Assertions.assertThat(joinable).isTrue();
    }

    @Test
    @DisplayName("스터디 매니저는 가입가능 조건을 만족하지 않는다.")
    void isJoinable_with_already_joined_manager() {
        // given
        User user = User.builder()
                .name("이름1")
                .email("이메일1")
                .role(Role.GUEST)
                .build();

        User guest = User.builder()
                .name("게스트1")
                .email("게스트이메일1")
                .role(Role.GUEST)
                .build();

        Study study = Study.builder()
                .title("제목")
                .path("path")
                .build();
        study.publish(Clock.systemDefaultZone());
        StudyManager manager = StudyManager.createManager(user, study);
        study.addManager(manager);

        // when
        study.isJoinable(user);
    }

    @Test
    @DisplayName("이미 가입한 멤버는 가입조건을 만족하지 않는다.")
    void isJoinable_with_already_joined_member() {
        // given
        User user = User.builder()
                .name("이름1")
                .email("이메일1")
                .role(Role.GUEST)
                .build();

        User guest = User.builder()
                .name("게스트1")
                .email("게스트이메일1")
                .role(Role.GUEST)
                .build();

        Study study = Study.builder()
                .title("제목")
                .path("path")
                .build();
        study.publish(Clock.systemDefaultZone());
        StudyManager manager = StudyManager.createManager(user, study);
        study.addManager(manager);

        StudyMember member = StudyMember.createMember(guest, study);
        study.addMember(member);

        // when
        study.isJoinable(guest);
    }

    @Test
    @DisplayName("studyTag Set을 인자로 전달하여 study의 해당 studyTag 추가")
    void addStudyTags() {
        // given
        Study study = Study.builder()
                .title("제목")
                .path("path")
                .build();
        study.publish(Clock.systemDefaultZone());

        Set<StudyTag> studyTags = createTags(3).stream()
                .map(tag -> StudyTag.builder()
                        .study(study)
                        .tag(tag)
                        .build())
                .collect(Collectors.toSet());

        // when
        study.addStudyTags(studyTags);

        // then
        Assertions.assertThat(study.getStudyTags()).hasSize(3)
                .extracting("study.title", "tag.title")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("제목", "tag1"),
                        Tuple.tuple("제목", "tag2"),
                        Tuple.tuple("제목", "tag3")
                );
    }

    @Test
    @DisplayName("studyZone Set을 인자로 전달하여 study의 해당 studyZone 추가")
    void addStudyZones() {
        // given
        Study study = Study.builder()
                .title("제목")
                .path("path")
                .build();
        study.publish(Clock.systemDefaultZone());

        Set<StudyZone> studyZones = createZones(3).stream()
                .map(zone -> StudyZone.builder()
                        .study(study)
                        .zone(zone)
                        .build())
                .collect(Collectors.toSet());

        // when
        study.addStudyZones(studyZones);

        // then
        Assertions.assertThat(study.getStudyZones()).hasSize(3)
                .extracting("study.title", "zone.city")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("제목", "city1"),
                        Tuple.tuple("제목", "city2"),
                        Tuple.tuple("제목", "city3")
                );
    }

    @Test
    @DisplayName("studyTag Set을 인자로 전달하여 study의 해당 studyTag 삭제")
    void removeStudyTags() {
        // given
        Study study = Study.builder()
                .title("제목")
                .path("path")
                .build();
        study.publish(Clock.systemDefaultZone());

        Set<StudyTag> studyTags = createTags(5).stream()
                .map(tag -> StudyTag.builder()
                        .study(study)
                        .tag(tag)
                        .build())
                .collect(Collectors.toSet());
        study.addStudyTags(studyTags);

        List<StudyTag> tagsToRemove = studyTags.stream()
                .sorted(Comparator.comparing(studyTag -> studyTag.getTag().getTitle()))
                .toList().subList(0, 3);

        // when
        study.removeStudyTags(new HashSet<>(tagsToRemove));

        // then
        Assertions.assertThat(study.getStudyTags()).hasSize(2)
                .extracting("study.title", "tag.title")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("제목", "tag4"),
                        Tuple.tuple("제목", "tag5")
                );
    }

    @Test
    @DisplayName("studyZone Set을 인자로 전달하여 study의 해당 studyZone 삭제")
    void removeStudyZones() {
        // given
        Study study = Study.builder()
                .title("제목")
                .path("path")
                .build();
        study.publish(Clock.systemDefaultZone());

        Set<StudyZone> studyZones = createZones(5).stream()
                .map(zone -> StudyZone.builder()
                        .study(study)
                        .zone(zone)
                        .build())
                .collect(Collectors.toSet());
        study.addStudyZones(studyZones);

        List<StudyZone> zonesToRemove = studyZones.stream()
                .sorted(Comparator.comparing(studyZone -> studyZone.getZone().getCity()))
                .toList().subList(0, 3);


        // when
        study.removeStudyZones(new HashSet<>(zonesToRemove));

        // then
        Assertions.assertThat(study.getStudyZones()).hasSize(2)
                .extracting("study.title", "zone.city")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("제목", "city4"),
                        Tuple.tuple("제목", "city5")
                );
    }

    @Test
    @DisplayName("toEditor로 StudyEditorBuilder를 반환")
    void toEditor() {
        // given
        Study study = Study.builder()
                .title("제목")
                .path("path")
                .build();

        // when
        StudyEditor studyEditor = study.toEditor().build();

        Assertions.assertThat(studyEditor.getTitle()).isEqualTo("제목");
        Assertions.assertThat(studyEditor.getPath()).isEqualTo("path");
    }

    @Test
    @DisplayName(" studyEditor를 인자로 받아 스터디 수정")
    void edit() {
        // given
        Study study = Study.builder()
                .title("제목")
                .path("path")
                .build();

        StudyEditor studyEditor = study.toEditor()
                .title("수정제목")
                .path("수정path")
                .build();

        // when
        study.edit(studyEditor);

        // then
        Assertions.assertThat(studyEditor.getTitle()).isEqualTo("수정제목");
        Assertions.assertThat(studyEditor.getPath()).isEqualTo("수정path");
    }

    @Test
    @DisplayName("모집 시작시 recruiting 상태 true로 변경")
    void startRecruit() {
        // given
        Study study = Study.builder()
                .title("제목")
                .path("path")
                .build();
        study.publish(Clock.systemDefaultZone());

        // when
        study.startRecruit(LocalDateTime.now(Clock.systemDefaultZone()));

        // then
        Assertions.assertThat(study.isRecruiting()).isTrue();
    }

    @Test
    @DisplayName("모집 중인 상태에서 다시 모집 시도하면 예외 발생")
    void startRecruit_after_recruiting() {
        // given
        Study study = Study.builder()
                .title("제목")
                .path("path")
                .build();
        study.publish(Clock.systemDefaultZone());

        study.startRecruit(LocalDateTime.now(Clock.systemDefaultZone()));

        // expected
        Assertions.assertThat(study.isRecruiting()).isTrue();
        Assertions.assertThatThrownBy(() -> study.startRecruit(LocalDateTime.now(Clock.systemDefaultZone())))
                .isInstanceOf(InvalidRecruitmentStateException.class);
    }

    @Test
    @DisplayName("공개되지 않은 스터디 모집 시도시 예외 발생")
    void startRecruit_with_unpublished_study() {
        // given
        Study study = Study.builder()
                .title("제목")
                .path("path")
                .build();

        // expected
        Assertions.assertThatThrownBy(() -> study.startRecruit(LocalDateTime.now(Clock.systemDefaultZone())))
                .isInstanceOf(InvalidRecruitmentStateException.class);
    }

    @Test
    @DisplayName("모집 상태 변경 1시간 지나지 않은 상태에서 다시 변경 시도시 예외 발생")
    void startRecruit_before_1Hour() {
        // given
        Study study = Study.builder()
                .title("제목")
                .path("path")
                .build();
        study.publish(Clock.systemDefaultZone());

        study.startRecruit(LocalDateTime.now(Clock.systemDefaultZone()));

        // expected
        Assertions.assertThatThrownBy(() -> study.stopRecruit(LocalDateTime.now(Clock.systemDefaultZone())))
                .isInstanceOf(InvalidRecruitmentStateException.class);
    }

    @Test
    @DisplayName("종료된 스터디에 모집 시도시 예외 발생")
    void startRecruit_with_closed_study() {
        // given
        Study study = Study.builder()
                .title("제목")
                .path("path")
                .build();
        study.publish(Clock.systemDefaultZone());
        study.close(Clock.systemDefaultZone());

        // expected
        Assertions.assertThatThrownBy(() -> study.startRecruit(LocalDateTime.now(Clock.systemDefaultZone())))
                .isInstanceOf(InvalidRecruitmentStateException.class);
    }

    @Test
    @DisplayName("성공적으로 모집 종료시 스터디의 recruiting을 false로 변경한다.")
    void stopRecruit() {
        Study study = Study.builder()
                .title("제목")
                .path("path")
                .build();
        study.publish(Clock.systemDefaultZone());

        study.startRecruit(LocalDateTime.now(Clock.systemDefaultZone()));

        // when
        study.stopRecruit(LocalDateTime.now(Clock.offset(Clock.systemDefaultZone(), Duration.ofMinutes(61))));

        //then
        Assertions.assertThat(study.isRecruiting()).isFalse();
    }

    @Test
    @DisplayName("모집 중이 아닌 상태에서 모집 종료시도하면 예외 발생")
    void stopRecruit_after_recruiting() {
        // given
        Study study = Study.builder()
                .title("제목")
                .path("path")
                .build();
        study.publish(Clock.systemDefaultZone());

        // expected
        Assertions.assertThat(study.isRecruiting()).isFalse();
        Assertions.assertThatThrownBy(() -> study.stopRecruit(LocalDateTime.now(Clock.systemDefaultZone())))
                .isInstanceOf(InvalidRecruitmentStateException.class);
    }

    @Test
    @DisplayName("공개되지 않은 스터디 모집 종료 시도시 예외 발생")
    void stopRecruit_with_unpublished_study() {
        // given
        Study study = Study.builder()
                .title("제목")
                .path("path")
                .build();

        // expected
        Assertions.assertThatThrownBy(() -> study.stopRecruit(LocalDateTime.now(Clock.systemDefaultZone())))
                .isInstanceOf(InvalidRecruitmentStateException.class);
    }

    @Test
    @DisplayName("모집 상태 변경 1시간 지나지 않은 상태에서 다시 변경 시도시 예외 발생")
    void stopRecruit_before_1Hour() {
        // given
        Study study = Study.builder()
                .title("제목")
                .path("path")
                .build();
        study.publish(Clock.systemDefaultZone());

        study.startRecruit(LocalDateTime.now(Clock.systemDefaultZone()));

        // expected
        Assertions.assertThatThrownBy(() -> study.stopRecruit(LocalDateTime.now(Clock.systemDefaultZone())))
                .isInstanceOf(InvalidRecruitmentStateException.class);
    }

    @Test
    @DisplayName("종료된 스터디에 모집 시도시 예외 발생")
    void stopRecruit_with_closed_study() {
        // given
        Study study = Study.builder()
                .title("제목")
                .path("path")
                .build();
        study.publish(Clock.systemDefaultZone());
        study.close(Clock.systemDefaultZone());

        // expected
        Assertions.assertThatThrownBy(() -> study.stopRecruit(LocalDateTime.now(Clock.systemDefaultZone())))
                .isInstanceOf(InvalidRecruitmentStateException.class);
    }

    @Test
    @DisplayName("recruiting 상태 반환")
    void isRecruiting() {
        // given
        Study study = Study.builder()
                .title("제목")
                .path("path")
                .build();
        study.publish(Clock.systemDefaultZone());
        study.startRecruit(LocalDateTime.now(Clock.systemDefaultZone()));

        // when
        boolean recruiting = study.isRecruiting();

        // then
        Assertions.assertThat(recruiting).isTrue();
    }

    @Test
    @DisplayName("publish 상태 true 반환")
    void isPublished() {
        // given
        Study study = Study.builder()
                .title("제목")
                .path("path")
                .build();
        study.publish(Clock.systemDefaultZone());

        // when
        boolean published = study.isPublished();

        // then
        Assertions.assertThat(published).isTrue();
    }

    @Test
    @DisplayName("close 상태 반환")
    void isClosed() {
        // given
        Study study = Study.builder()
                .title("제목")
                .path("path")
                .build();
        study.publish(Clock.systemDefaultZone());
        study.close(Clock.systemDefaultZone());

        // when
        boolean closed = study.isClosed();

        // then
        Assertions.assertThat(closed).isTrue();
    }
}