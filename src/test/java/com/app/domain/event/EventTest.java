package com.app.domain.event;

import com.app.TestUtils;
import com.app.domain.study.Study;
import com.app.domain.study.StudyEditor;
import com.app.domain.user.User;
import com.app.domain.user.constant.Role;
import com.app.global.error.exception.InvalidEnrollmentStateException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDateTime;

class EventTest {

    @Test
    @DisplayName("아직 승인, 참석되지 않은 enrollment가 이벤트에 등록된 상태에서 canAccept하면 true 반환")
    void canAccept() {
        // given
        Clock clock = TestUtils.getFixedClock();
        LocalDateTime now = LocalDateTime.now(clock);
        Event event = createEvent(now);

        Enrollment enrollment = Enrollment.builder()
                .user(createUser(1))
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(false)
                .attended(false)
                .build();
        event.addEnrollment(enrollment);

        // when
        boolean canAccept = event.canAccept(enrollment);

        // then
        Assertions.assertThat(canAccept).isTrue();
    }

    @Test
    @DisplayName("이벤트에 등록되지 않은 enrollment에 대해 canAccept하면 false 반환")
    void canAccept_before_addEnrollment() {
        // given
        Clock clock = TestUtils.getFixedClock();
        LocalDateTime now = LocalDateTime.now(clock);
        Event event = createEvent(now);

        Enrollment enrollment = Enrollment.builder()
                .user(createUser(1))
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(false)
                .attended(false)
                .build();

        // when
        boolean canAccept = event.canAccept(enrollment);

        // then
        Assertions.assertThat(canAccept).isFalse();
    }

    @Test
    @DisplayName("이미 승인(accept)된 enrollment에 대해 canAccept하면 false 반환")
    void canAccept_after_acceptEnrollment() {
        // given
        Clock clock = TestUtils.getFixedClock();
        LocalDateTime now = LocalDateTime.now(clock);
        Event event = createEvent(now);

        Enrollment enrollment = Enrollment.builder()
                .user(createUser(1))
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(true)
                .attended(false)
                .build();

        // when
        boolean canAccept = event.canAccept(enrollment);

        // then
        Assertions.assertThat(canAccept).isFalse();
    }

    @Test
    @DisplayName("이미 참석(attend)된 enrollment에 대해 canAccept하면 false 반환")
    void canAccept_after_attendEnrollment() {
        // given
        Clock clock = TestUtils.getFixedClock();
        LocalDateTime now = LocalDateTime.now(clock);
        Event event = createEvent(now);

        Enrollment enrollment = Enrollment.builder()
                .user(createUser(1))
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(true)
                .attended(true)
                .build();

        // when
        boolean canAccept = event.canAccept(enrollment);

        // then
        Assertions.assertThat(canAccept).isFalse();
    }

    @Test
    @DisplayName("승인되었지만 아직 참석되지 않은 enrollment가 이벤트에 등록된 상태에서 canReject하면 true 반환")
    void canReject() {
        // given
        Clock clock = TestUtils.getFixedClock();
        LocalDateTime now = LocalDateTime.now(clock);
        Event event = createEvent(now);

        Enrollment enrollment = Enrollment.builder()
                .user(createUser(1))
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(true)
                .attended(false)
                .build();
        event.addEnrollment(enrollment);

        // when
        boolean canReject = event.canReject(enrollment);

        // then
        Assertions.assertThat(canReject).isTrue();
    }

    @Test
    @DisplayName("이벤트에 등록되지 않은 enrollment에 대해 canReject하면 false 반환")
    void canReject_before_addEnrollment() {
        // given
        Clock clock = TestUtils.getFixedClock();
        LocalDateTime now = LocalDateTime.now(clock);
        Event event = createEvent(now);

        Enrollment enrollment = Enrollment.builder()
                .user(createUser(1))
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(false)
                .attended(false)
                .build();

        // when
        boolean canReject = event.canReject(enrollment);

        // then
        Assertions.assertThat(canReject).isFalse();
    }

    @Test
    @DisplayName("승인(accept)되지않은 enrollment에 대해 canReject하면 false 반환")
    void canReject_after_acceptEnrollment() {
        // given
        Clock clock = TestUtils.getFixedClock();
        LocalDateTime now = LocalDateTime.now(clock);
        Event event = createEvent(now);

        Enrollment enrollment = Enrollment.builder()
                .user(createUser(1))
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(false)
                .attended(false)
                .build();
        event.addEnrollment(enrollment);

        // when
        boolean canReject = event.canReject(enrollment);

        // then
        Assertions.assertThat(canReject).isFalse();
    }

    @Test
    @DisplayName("이미 참석(attend)된 enrollment에 대해 canReject하면 false 반환")
    void canReject_after_attendEnrollment() {
        // given
        Clock clock = TestUtils.getFixedClock();
        LocalDateTime now = LocalDateTime.now(clock);
        Event event = createEvent(now);

        Enrollment enrollment = Enrollment.builder()
                .user(createUser(1))
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(true)
                .attended(true)
                .build();
        event.addEnrollment(enrollment);

        // when
        boolean canReject = event.canReject(enrollment);

        // then
        Assertions.assertThat(canReject).isFalse();
    }

    @Test
    @DisplayName("enrollment를 추가하면 event의 list에 등록된다.")
    void addEnrollment() {
        // given
        Clock clock = TestUtils.getFixedClock();
        LocalDateTime now = LocalDateTime.now(clock);
        Event event = createEvent(now);

        Enrollment enrollment = Enrollment.builder()
                .user(createUser(1))
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(true)
                .attended(true)
                .build();

        // when
        event.addEnrollment(enrollment);

        // then
        Assertions.assertThat(event.getEnrollments()).hasSize(1)
                .extracting("event.title")
                .containsExactlyInAnyOrder("이벤트제목");
    }

    @Test
    @DisplayName("중복된 enrollment를 추가하면 event의 list에 등록되지 않는다.")
    void addEnrollment_duplicated_enrollment() {
        // given
        Clock clock = TestUtils.getFixedClock();
        LocalDateTime now = LocalDateTime.now(clock);
        Event event = createEvent(now);

        Enrollment enrollment = Enrollment.builder()
                .user(createUser(1))
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(true)
                .attended(true)
                .build();

        // when
        event.addEnrollment(enrollment);
        event.addEnrollment(enrollment);

        // then
        Assertions.assertThat(event.getEnrollments()).hasSize(1)
                .extracting("event.title")
                .containsExactlyInAnyOrder("이벤트제목");
    }

    @Test
    @DisplayName("enrollment를 삭제하면 event의 리스트에서 삭제된다.")
    void removeEnrollment() {
        // given
        Clock clock = TestUtils.getFixedClock();
        LocalDateTime now = LocalDateTime.now(clock);
        Event event = createEvent(now);

        Enrollment enrollment = Enrollment.builder()
                .user(createUser(1))
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(true)
                .attended(true)
                .build();

        event.addEnrollment(enrollment);
        // when
        event.removeEnrollment(enrollment);
        // then
        Assertions.assertThat(event.getEnrollments()).hasSize(0);
        Assertions.assertThat(enrollment.getEvent()).isNull();
    }

    @Test
    @DisplayName("이벤트에 등록되어 있고 아직 승인(accept), 참석(attend)하지 않은 enrollment를 accpet하면 값을 true로 변경")
    void accept() {
        // given
        Clock clock = TestUtils.getFixedClock();
        LocalDateTime now = LocalDateTime.now(clock);
        Event event = createEvent(now);

        Enrollment enrollment = Enrollment.builder()
                .user(createUser(1))
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(false)
                .attended(false)
                .build();

        event.addEnrollment(enrollment);
        // when
        event.accept(enrollment);
        // then
        Assertions.assertThat(event.getEnrollments()).hasSize(1)
                .extracting("accepted")
                .containsExactlyInAnyOrder(true);
    }

    @Test
    @DisplayName("enrollment를 이벤트에 등록 전에 accept 시도하면 예외 발생")
    void accept_before_add_enrollment() {
        // given
        Clock clock = TestUtils.getFixedClock();
        LocalDateTime now = LocalDateTime.now(clock);
        Event event = createEvent(now);

        Enrollment enrollment = Enrollment.builder()
                .user(createUser(1))
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(false)
                .attended(false)
                .build();

        // expected
        Assertions.assertThatThrownBy(() -> event.accept(enrollment)).isInstanceOf(InvalidEnrollmentStateException.class);
    }

    @Test
    @DisplayName("이미 승인(accept)한 enrollment를 다시 accept 시도하면 예외 발생")
    void accept_with_accepted_enrollment() {
        // given
        Clock clock = TestUtils.getFixedClock();
        LocalDateTime now = LocalDateTime.now(clock);
        Event event = createEvent(now);

        Enrollment enrollment = Enrollment.builder()
                .user(createUser(1))
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(true)
                .attended(false)
                .build();

        // expected
        Assertions.assertThatThrownBy(() -> event.accept(enrollment)).isInstanceOf(InvalidEnrollmentStateException.class);
    }

    @Test
    @DisplayName("이미 참석(attend)한 enrollment를 다시 accept 시도하면 예외 발생")
    void accept_with_attended_enrollment() {
        // given
        Clock clock = TestUtils.getFixedClock();
        LocalDateTime now = LocalDateTime.now(clock);
        Event event = createEvent(now);

        Enrollment enrollment = Enrollment.builder()
                .user(createUser(1))
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(true)
                .attended(true)
                .build();

        // expected
        Assertions.assertThatThrownBy(() -> event.accept(enrollment)).isInstanceOf(InvalidEnrollmentStateException.class);
    }

    @Test
    @DisplayName("승인(accept)된 enrollment를 reject하면 accepted를 false로 변경")
    void reject() {
        // given
        Clock clock = TestUtils.getFixedClock();
        LocalDateTime now = LocalDateTime.now(clock);
        Event event = createEvent(now);

        Enrollment enrollment = Enrollment.builder()
                .user(createUser(1))
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(true)
                .attended(false)
                .build();

        event.addEnrollment(enrollment);
        // when
        event.reject(enrollment);
        // then
        Assertions.assertThat(event.getEnrollments()).hasSize(1)
                .extracting("accepted")
                .containsExactlyInAnyOrder(false);
    }

    @Test
    @DisplayName("enrollment를 이벤트에 등록 전에 reject 시도하면 예외 발생")
    void reject_before_add_enrollment() {
        // given
        Clock clock = TestUtils.getFixedClock();
        LocalDateTime now = LocalDateTime.now(clock);
        Event event = createEvent(now);

        Enrollment enrollment = Enrollment.builder()
                .user(createUser(1))
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(true)
                .attended(false)
                .build();

        // expected
        Assertions.assertThatThrownBy(() -> event.reject(enrollment)).isInstanceOf(InvalidEnrollmentStateException.class);
    }

    @Test
    @DisplayName("승인(accept)되지 않은 enrollment를 reject 시도하면 예외 발생")
    void reject_with_accepted_enrollment() {
        // given
        Clock clock = TestUtils.getFixedClock();
        LocalDateTime now = LocalDateTime.now(clock);
        Event event = createEvent(now);

        Enrollment enrollment = Enrollment.builder()
                .user(createUser(1))
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(true)
                .attended(true)
                .build();

        // expected
        Assertions.assertThatThrownBy(() -> event.reject(enrollment)).isInstanceOf(InvalidEnrollmentStateException.class);
    }

    @Test
    @DisplayName("이미 참석(attend)한 enrollment를 reject 시도하면 예외 발생")
    void reject_with_attended_enrollment() {
        // given
        Clock clock = TestUtils.getFixedClock();
        LocalDateTime now = LocalDateTime.now(clock);
        Event event = createEvent(now);

        Enrollment enrollment = Enrollment.builder()
                .user(createUser(1))
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(true)
                .attended(true)
                .build();

        // expected
        Assertions.assertThatThrownBy(() -> event.reject(enrollment)).isInstanceOf(InvalidEnrollmentStateException.class);
    }

    @Test
    @DisplayName("toEditor로 eventEditorBuilder를 반환")
    void toEditor() {
        // given
        Clock clock = TestUtils.getFixedClock();
        LocalDateTime now = LocalDateTime.now(clock);
        Event event = createEvent(now);

        // when
        EventEditor eventEditor = event.toEditor().build();

        // then
        Assertions.assertThat(eventEditor.getTitle()).isEqualTo("이벤트제목");
    }

    @Test
    @DisplayName("eventEditor를 인자로 받아 스터디 수정")
    void edit() {
        // given
        Clock clock = TestUtils.getFixedClock();
        LocalDateTime now = LocalDateTime.now(clock);
        Event event = createEvent(now);

        EventEditor eventEditor = event.toEditor()
                .title("수정제목")
                .build();

        // when
        event.edit(eventEditor);

        // then
        Assertions.assertThat(eventEditor.getTitle()).isEqualTo("수정제목");
    }

    @Test
    @DisplayName("전체 허용 인원 수보다 현재 accept된 인원이 적다면 true 반환 ")
    void isAbleToAccept() {
        // given
        Clock clock = TestUtils.getFixedClock();
        LocalDateTime now = LocalDateTime.now(clock);

        Event event = createEvent(now);
        for (int i = 1; i <= 3; i++) {
            Enrollment enrollment = Enrollment.builder()
                    .user(createUser(i))
                    .event(event)
                    .enrolledAt(now.plusMinutes(10))
                    .accepted(true)
                    .attended(true)
                    .build();
            event.addEnrollment(enrollment);
        }

        // when
        boolean ableToAccept = event.isAbleToAccept();

        // then
        Assertions.assertThat(ableToAccept).isFalse();
    }

    private Event createEvent(LocalDateTime now) {

        return Event.builder()
                .title("이벤트제목")
                .endEnrollmentDateTime(now.plusMinutes(1))
                .startDateTime(now.plusMinutes(2))
                .endDateTime(now.plusMinutes(3))
                .limitOfEnrollments(3)
                .build();
    }

    private User createUser(int alias) {

        return User.builder()
                .name("testName" + alias)
                .email("testEmail" + alias + "@gmail.com")
                .role(Role.GUEST)
                .build();
    }
}