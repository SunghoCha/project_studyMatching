package com.app.domain.event.service;

import com.app.WithAccount;
import com.app.config.TestClockConfig;
import com.app.domain.event.Enrollment;
import com.app.domain.event.Event;
import com.app.domain.event.dto.*;
import com.app.domain.event.repository.EnrollmentRepository;
import com.app.domain.event.repository.EventRepository;
import com.app.domain.study.Study;
import com.app.domain.study.repository.StudyRepository;
import com.app.domain.study.service.StudyService;
import com.app.domain.study.studyManager.StudyManager;
import com.app.domain.study.studyManager.repository.StudyManagerRepository;
import com.app.domain.study.studyMember.StudyMember;
import com.app.domain.study.studyMember.repository.StudyMemberRepository;
import com.app.domain.user.User;
import com.app.domain.user.constant.Role;
import com.app.domain.user.repository.UserRepository;
import com.app.global.error.exception.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Import(TestClockConfig.class)
@Transactional
@SpringBootTest
class EventServiceTest {

    private static final Long INVALID_USER_ID = -1L;

    @Autowired
    EventService eventService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    StudyRepository studyRepository;

    @Autowired
    StudyManagerRepository studyManagerRepository;

    @Autowired
    StudyMemberRepository studyMemberRepository;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Autowired
    Clock clock;
    private StudyService studyService;

    @Test
    @WithAccount
    @DisplayName("올바른 입력으로 이벤트를 생성한다")
    void createEvent_with_correct_input() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);

        EventCreateRequest request = EventCreateRequest.builder()
                .title("이벤트1")
                .description("설명1")
                .endEnrollmentDateTime(now.plusMinutes(1))
                .startDateTime(now.plusMinutes(2))
                .endDateTime(now.plusMinutes(3))
                .limitOfEnrollments(2)
                .build();

        // when
        EventCreateResponse response = eventService.createEvent(user.getId(), path, request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getEventId()).isNotNull();
        assertThat(response.getTitle()).isEqualTo(request.getTitle());
        assertThat(response.getDescription()).isEqualTo(request.getDescription());
        assertThat(response.getEndEnrollmentDateTime()).isEqualTo(request.getEndEnrollmentDateTime());
        assertThat(response.getStartDateTime()).isEqualTo(request.getStartDateTime());
        assertThat(response.getEndDateTime()).isEqualTo(request.getEndDateTime());
        assertThat(response.getLimitOfEnrollments()).isEqualTo(request.getLimitOfEnrollments());
    }

    @Test
    @DisplayName("스터디가 활성화되지 않은 상태에서 이벤트 생성 시 예외를 발생시킨다")
    void createEvent_with_unpublished_study() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);

        EventCreateRequest request = EventCreateRequest.builder()
                .title("이벤트1")
                .description("설명1")
                .endEnrollmentDateTime(now.plusMinutes(1))
                .startDateTime(now.plusMinutes(2))
                .endDateTime(now.plusMinutes(3))
                .limitOfEnrollments(2)
                .build();

        // expected
        assertThatThrownBy(() -> eventService.createEvent(user.getId(), path, request))
                .isInstanceOf(InvalidStudyPublishStateException.class);
    }

    @Test
    @DisplayName("스터디가 종료된 상태에서 이벤트 생성 시 예외를 발생시킨다")
    void createEvent_with_closed_study() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);

        EventCreateRequest request = EventCreateRequest.builder()
                .title("이벤트1")
                .description("설명1")
                .endEnrollmentDateTime(now.plusMinutes(1))
                .startDateTime(now.plusMinutes(2))
                .endDateTime(now.plusMinutes(3))
                .limitOfEnrollments(2)
                .build();
        study.publish(clock);
        study.close(clock);

        // expected
        assertThatThrownBy(() -> eventService.createEvent(user.getId(), path, request))
                .isInstanceOf(InvalidStudyPublishStateException.class);
    }

    @Test
    @DisplayName("이벤트 ID로 이벤트를 조회한다")
    void getEvent() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);

        Event event = createAndSaveEvent(now, study, "이벤트");

        // when
        EventResponse response = eventService.getEvent(user.getId(), event.getId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.getEventId()).isNotNull();
        assertThat(response.getTitle()).isEqualTo(event.getTitle());
        assertThat(response.getDescription()).isEqualTo(event.getDescription());
        assertThat(response.getEndEnrollmentDateTime()).isEqualTo(event.getEndEnrollmentDateTime());
        assertThat(response.getStartDateTime()).isEqualTo(event.getStartDateTime());
        assertThat(response.getEndDateTime()).isEqualTo(event.getEndDateTime());
        assertThat(response.getLimitOfEnrollments()).isEqualTo(event.getLimitOfEnrollments());
    }

    @Test
    @DisplayName("잘못된 이벤트 ID로 이벤트 조회시 예외 발생")
    void getEvent_with_wrong_eventId() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);

        Event event = createAndSaveEvent(now, study, "이벤트");

        // expected
        assertThatThrownBy(() -> eventService.getEvent(user.getId(), INVALID_USER_ID)).isInstanceOf(EventNotFoundException.class);
    }

    @Test
    @DisplayName("스터디 경로로 이벤트 목록을 조회한다 (새 이벤트와 지난 이벤트로 분류)")
    void getEvents() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);

        for (int i = 1; i <= 3; i++) {
            createAndSaveEvent(now, study, "이벤트" + i);
            createAndSaveOldEvent(now, study, "지난이벤트" + i);
        }

        // when
        EventsResponse response = eventService.getEvents(path);

        // then
        assertThat(response).isNotNull();

        assertThat(response.getNewEvents()).isNotEmpty();
        assertThat(response.getNewEvents()).hasSize(3);
        for (int i = 0; i < 3; i++) {
            EventSummaryResponse eventResponse = response.getNewEvents().get(i);
            assertThat(eventResponse.getTitle()).isEqualTo("제목이벤트" + (i + 1));
            assertThat(eventResponse.getDescription()).isEqualTo("소개이벤트" + (i + 1));
        }

        assertThat(response.getOldEvents()).isNotEmpty();
        assertThat(response.getOldEvents()).hasSize(3);
        for (int i = 0; i < 3; i++) {
            EventSummaryResponse eventResponse = response.getOldEvents().get(i);
            assertThat(eventResponse.getTitle()).isEqualTo("제목지난이벤트" + (i + 1));
            assertThat(eventResponse.getDescription()).isEqualTo("소개지난이벤트" + (i + 1));
        }
    }

    @Test
    @DisplayName("잘못된 스터디 path로 조회하면 예외 발생")
    void getEvents_with_wrong_path() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);

        for (int i = 1; i <= 3; i++) {
            createAndSaveEvent(now, study, "이벤트" + i);
            createAndSaveOldEvent(now, study, "지난이벤트" + i);
        }

        // expected
        assertThatThrownBy(() -> eventService.getEvents("wrong")).isInstanceOf(StudyNotFoundException.class);
    }

    @Test
    @DisplayName("올바른 입력으로 이벤트를 수정한다. (참가날짜, 인원수..)")
    void updateEvent() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);

        Event event = createAndSaveEvent(now, study, "이벤트");

        EventUpdateRequest request = EventUpdateRequest.builder()
                .title("수정제목1")
                .description("수정소개1")
                .endEnrollmentDateTime(now.plusMinutes(10))
                .startDateTime(now.plusMinutes(20))
                .endDateTime(now.plusMinutes(30))
                .limitOfEnrollments(5)
                .build();

        // when
        EventUpdateResponse response = eventService.updateEvent(user.getId(), event.getId(), path, request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getEventId()).isNotNull();
        assertThat(response.getTitle()).isEqualTo("수정제목1");
        assertThat(response.getDescription()).isEqualTo("수정소개1");
        assertThat(response.getEndEnrollmentDateTime()).isEqualTo(now.plusMinutes(10));
        assertThat(response.getStartDateTime()).isEqualTo(now.plusMinutes(20));
        assertThat(response.getEndDateTime()).isEqualTo(now.plusMinutes(30));
        assertThat(response.getLimitOfEnrollments()).isEqualTo(5);
    }

    @Test
    @DisplayName("잘못된 이벤트 ID로 이벤트 수정 시도시 예외 발생")
    void updateEvent_with_wrong_eventId() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);

        Event event = createAndSaveEvent(now, study, "이벤트");

        EventUpdateRequest request = EventUpdateRequest.builder()
                .title("수정제목1")
                .description("수정소개1")
                .endEnrollmentDateTime(now.plusMinutes(10))
                .startDateTime(now.plusMinutes(20))
                .endDateTime(now.plusMinutes(30))
                .limitOfEnrollments(5)
                .build();

        // expected
        assertThatThrownBy(() -> eventService.updateEvent(user.getId(), INVALID_USER_ID, path, request)).isInstanceOf(EventNotFoundException.class);
    }

    @Test
    @DisplayName("잘못된 스터디 path로 이벤트 수정 시도시 예외 발생")
    void updateEvent_with_wrong_path() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);

        Event event = createAndSaveEvent(now, study, "이벤트");

        EventUpdateRequest request = EventUpdateRequest.builder()
                .title("수정제목1")
                .description("수정소개1")
                .endEnrollmentDateTime(now.plusMinutes(10))
                .startDateTime(now.plusMinutes(20))
                .endDateTime(now.plusMinutes(30))
                .limitOfEnrollments(5)
                .build();

        // expected
        assertThatThrownBy(() -> eventService.updateEvent(user.getId(), event.getId(), "wrong", request))
                .isInstanceOf(StudyNotFoundException.class);
    }

    @Test
    @DisplayName("잘못된 userId로 이벤트 수정 시도시 예외 발생")
    void updateEvent_with_wrong_userId() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);

        Event event = createAndSaveEvent(now, study, "이벤트");

        EventUpdateRequest request = EventUpdateRequest.builder()
                .title("수정제목1")
                .description("수정소개1")
                .endEnrollmentDateTime(now.plusMinutes(10))
                .startDateTime(now.plusMinutes(20))
                .endDateTime(now.plusMinutes(30))
                .limitOfEnrollments(5)
                .build();

        // expected
        assertThatThrownBy(() -> eventService.updateEvent(INVALID_USER_ID, event.getId(), path, request))
                .isInstanceOf(EventNotFoundException.class);
    }

    @Test
    @DisplayName("이벤트 삭제 요청 시 해당 이벤트가 성공적으로 삭제된다")
    void deleteEvent() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);

        Event event = createAndSaveEvent(now, study, "이벤트");

        // when
        eventService.deleteEvent(user.getId(), event.getId(), path);

        // then
        boolean eventExists = eventRepository.findById(event.getId()).isPresent();
        Assertions.assertThat(eventExists).isFalse();
    }

    @Test
    @DisplayName("잘못된 userId로 이벤트 삭제 요청 시 예외 발생")
    void deleteEvent_with_wrong_userId() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);

        Event event = createAndSaveEvent(now, study, "이벤트");

        // expected
        assertThatThrownBy(() -> eventService.deleteEvent(INVALID_USER_ID, event.getId(), path)).isInstanceOf(EventNotFoundException.class);
    }

    @Test
    @DisplayName("잘못된 eventId로 이벤트 삭제 요청 시 예외 발생")
    void deleteEvent_with_wrong_path() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);

        Event event = createAndSaveEvent(now, study, "이벤트");

        // expected
        assertThatThrownBy(() -> eventService.deleteEvent(user.getId(), INVALID_USER_ID, path))
                .isInstanceOf(EventNotFoundException.class);
    }

    @Test
    @DisplayName("잘못된 스터디 path로 이벤트 삭제 요청 시 예외 발생")
    void deleteEvent_with_wrong_eventId() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);

        Event event = createAndSaveEvent(now, study, "이벤트");

        // expected
        assertThatThrownBy(() -> eventService.deleteEvent(user.getId(), event.getId(), "wrong"))
                .isInstanceOf(EventNotFoundException.class);
    }

    @Test
    @DisplayName("모임 참가 요청 시 새로운 참가(enrollment)를 성공적으로 생성한다")
    void createEnrollment() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);
        study.startRecruit(now);
        // 스터디 세팅
        User guest = createAndSaveUser("guest", Role.GUEST);
        StudyMember studyMember = StudyMember.createMember(guest, study);
        StudyMember savedMember = studyMemberRepository.save(studyMember);

        Event event = createAndSaveEvent(now, study, "이벤트");

        // when
        EnrollmentCreateResponse response = eventService.createEnrollment(guest.getId(), event.getId(), path);

        // then
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getEventId()).isNotNull();
        Assertions.assertThat(response.getEnrollmentId()).isNotNull();
        Assertions.assertThat(response.getEnrolledAt()).isEqualTo(LocalDateTime.now(clock));
    }

    @Test
    @DisplayName("공개 안한 스터디에 모임 참가 요청 시 예외 발생")
    void createEnrollment_with_unpublished_study() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        // 스터디 세팅
        User guest = createAndSaveUser("guest", Role.GUEST);
        StudyMember studyMember = StudyMember.createMember(guest, study);
        StudyMember savedMember = studyMemberRepository.save(studyMember);

        Event event = createAndSaveEvent(now, study, "이벤트");

        // expected
        assertThatThrownBy(() -> eventService.createEnrollment(guest.getId(), event.getId(), path))
                .isInstanceOf(InvalidStudyPublishStateException.class);
    }

    @Test
    @DisplayName("종료된 스터디에 모임 참가 요청 시 예외 발생")
    void createEnrollment_with_closed_study() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);
        study.close(clock);
        // 스터디 세팅
        User guest = createAndSaveUser("guest", Role.GUEST);
        StudyMember studyMember = StudyMember.createMember(guest, study);
        StudyMember savedMember = studyMemberRepository.save(studyMember);

        Event event = createAndSaveEvent(now, study, "이벤트");

        // expected
        assertThatThrownBy(() -> eventService.createEnrollment(guest.getId(), event.getId(), path))
                .isInstanceOf(InvalidStudyPublishStateException.class);
    }

    @Test
    @DisplayName("스터디 모집 중 아닐때 모임 참가 요청 시 예외 발생")
    void createEnrollment_before_recruiting() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);
        // 스터디 세팅
        User guest = createAndSaveUser("guest", Role.GUEST);
        StudyMember studyMember = StudyMember.createMember(guest, study);
        StudyMember savedMember = studyMemberRepository.save(studyMember);

        Event event = createAndSaveEvent(now, study, "이벤트");

        // expected
        assertThatThrownBy(() -> eventService.createEnrollment(guest.getId(), event.getId(), path))
                .isInstanceOf(InvalidRecruitmentStateException.class);
    }

    @Test
    @DisplayName("모임 참가 취소 요청 시 참가 정보(enrollment)가 성공적으로 삭제된다")
    void cancelEnrollment() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);
        study.startRecruit(now);
        // 스터디 세팅
        User guest = createAndSaveUser("guest", Role.GUEST);
        StudyMember studyMember = StudyMember.createMember(guest, study);
        StudyMember savedMember = studyMemberRepository.save(studyMember);

        Event event = createAndSaveEvent(now, study, "이벤트");
        Enrollment enrollment = Enrollment.builder()
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(true)
                .attended(false)
                .user(guest)
                .build();
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        event.addEnrollment(savedEnrollment);

        // when
        eventService.cancelEnrollment(guest.getId(), event.getId(), path);

        // then
        boolean enrollmentExists = enrollmentRepository.findById(savedEnrollment.getId()).isPresent();
        Assertions.assertThat(enrollmentExists).isFalse();
    }

    @Test
    @DisplayName("accept되지 않은 모임 참가 취소 요청 시 예외 발생")
    void cancelEnrollment_with_already_accepted() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);
        study.startRecruit(now);
        // 스터디 세팅
        User guest = createAndSaveUser("guest", Role.GUEST);
        StudyMember studyMember = StudyMember.createMember(guest, study);
        StudyMember savedMember = studyMemberRepository.save(studyMember);

        Event event = createAndSaveEvent(now, study, "이벤트");
        Enrollment enrollment = Enrollment.builder()
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(false)
                .attended(false)
                .user(guest)
                .build();
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        event.addEnrollment(savedEnrollment);

        // when
        assertThatThrownBy(() -> eventService.cancelEnrollment(guest.getId(), event.getId(), path))
                .isInstanceOf(InvalidEnrollmentStateException.class);
    }

    @Test
    @DisplayName("공개 안한 스터디에 모임 참가취소 요청 시 예외 발생")
    void cancelEnrollment_with_unpublished_study() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        // 스터디 세팅
        User guest = createAndSaveUser("guest", Role.GUEST);
        StudyMember studyMember = StudyMember.createMember(guest, study);
        StudyMember savedMember = studyMemberRepository.save(studyMember);

        Event event = createAndSaveEvent(now, study, "이벤트");
        Enrollment enrollment = Enrollment.builder()
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(true)
                .attended(false)
                .user(guest)
                .build();
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        event.addEnrollment(savedEnrollment);

        // expected
        assertThatThrownBy(() -> eventService.cancelEnrollment(guest.getId(), event.getId(), path))
                .isInstanceOf(InvalidStudyPublishStateException.class);
    }

    @Test
    @DisplayName("종료된 스터디에 모임 참가취소 요청 시 예외 발생")
    void cancelEnrollment_with_closed_study() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);
        study.close(clock);
        // 스터디 세팅
        User guest = createAndSaveUser("guest", Role.GUEST);
        StudyMember studyMember = StudyMember.createMember(guest, study);
        StudyMember savedMember = studyMemberRepository.save(studyMember);

        Event event = createAndSaveEvent(now, study, "이벤트");
        Enrollment enrollment = Enrollment.builder()
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(true)
                .attended(false)
                .user(guest)
                .build();
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        event.addEnrollment(savedEnrollment);

        // expected
        assertThatThrownBy(() -> eventService.cancelEnrollment(guest.getId(), event.getId(), path))
                .isInstanceOf(InvalidStudyPublishStateException.class);
    }

    @Test
    @DisplayName("이벤트 참석 중인 상태에서 취소 시도하면 예외 발생")
    void cancelEnrollment_after_attending() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);
        study.startRecruit(now);
        // 스터디 세팅
        User guest = createAndSaveUser("guest", Role.GUEST);
        StudyMember studyMember = StudyMember.createMember(guest, study);
        StudyMember savedMember = studyMemberRepository.save(studyMember);

        Event event = createAndSaveEvent(now, study, "이벤트");
        Enrollment enrollment = Enrollment.builder()
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(true)
                .attended(true)
                .user(guest)
                .build();
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        event.addEnrollment(savedEnrollment);

        // expected
        assertThatThrownBy(() -> eventService.cancelEnrollment(guest.getId(), event.getId(), path))
                .isInstanceOf(InvalidEnrollmentStateException.class);
    }

    @Test
    @DisplayName("스터디 모집 중 아닐때 모임 참가취소 요청 시 예외 발생")
    void cancelEnrollment_before_recruiting() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);
        // 스터디 세팅
        User guest = createAndSaveUser("guest", Role.GUEST);
        StudyMember studyMember = StudyMember.createMember(guest, study);
        StudyMember savedMember = studyMemberRepository.save(studyMember);

        Event event = createAndSaveEvent(now, study, "이벤트");
        Enrollment enrollment = Enrollment.builder()
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(true)
                .attended(true)
                .user(guest)
                .build();
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        event.addEnrollment(savedEnrollment);

        // expected
        assertThatThrownBy(() -> eventService.cancelEnrollment(guest.getId(), event.getId(), path))
                .isInstanceOf(InvalidRecruitmentStateException.class);
    }

    @Test
    @DisplayName("모임 참가 승인 요청 시 참가 정보(enrollment)의 승인 상태(accepted)를 true로 변경한다")
    void acceptEnrollment() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);
        study.startRecruit(now);
        // 스터디 세팅
        User guest = createAndSaveUser("guest", Role.GUEST);
        StudyMember studyMember = StudyMember.createMember(guest, study);
        StudyMember savedMember = studyMemberRepository.save(studyMember);

        Event event = createAndSaveEvent(now, study, "이벤트");
        Enrollment enrollment = Enrollment.builder()
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(false)
                .attended(false)
                .user(guest)
                .build();
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        event.addEnrollment(savedEnrollment);

        // when
        EnrollmentResponse response = eventService.acceptEnrollment(user.getId(), event.getId(), savedEnrollment.getId(), path);

        // then
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getEnrollmentId()).isNotNull();
        Assertions.assertThat(response.getEnrolledAt()).isEqualTo(now.plusMinutes(10));
        Assertions.assertThat(response.isAccepted()).isTrue();
        Assertions.assertThat(response.isAttended()).isFalse();
    }

    @Test
    @DisplayName("공개 안한 스터디에 모임 참가 승인 요청시 예외 발생")
    void acceptEnrollment_with_unpublished_study() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        // 스터디 세팅
        User guest = createAndSaveUser("guest", Role.GUEST);
        StudyMember studyMember = StudyMember.createMember(guest, study);
        StudyMember savedMember = studyMemberRepository.save(studyMember);

        Event event = createAndSaveEvent(now, study, "이벤트");
        Enrollment enrollment = Enrollment.builder()
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(false)
                .attended(false)
                .user(guest)
                .build();
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        event.addEnrollment(savedEnrollment);

        // expected
        assertThatThrownBy(() -> eventService.acceptEnrollment(user.getId(), event.getId(), enrollment.getId(), path))
                .isInstanceOf(InvalidStudyPublishStateException.class);
    }

    @Test
    @DisplayName("종료된 스터디에 모임 참가 승인 요청시 예외 발생")
    void acceptEnrollment_with_closed_study() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);
        study.close(clock);
        // 스터디 세팅
        User guest = createAndSaveUser("guest", Role.GUEST);
        StudyMember studyMember = StudyMember.createMember(guest, study);
        StudyMember savedMember = studyMemberRepository.save(studyMember);

        Event event = createAndSaveEvent(now, study, "이벤트");
        Enrollment enrollment = Enrollment.builder()
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(false)
                .attended(false)
                .user(guest)
                .build();
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        event.addEnrollment(savedEnrollment);

        // expected
        assertThatThrownBy(() -> eventService.acceptEnrollment(user.getId(), event.getId(), enrollment.getId(), path))
                .isInstanceOf(InvalidStudyPublishStateException.class);
    }

    @Test
    @DisplayName("스터디 모집 중 아닐때 모임 참가승인 요청 시 예외 발생")
    void acceptEnrollment_before_recruiting() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);
        // 스터디 세팅
        User guest = createAndSaveUser("guest", Role.GUEST);
        StudyMember studyMember = StudyMember.createMember(guest, study);
        StudyMember savedMember = studyMemberRepository.save(studyMember);

        Event event = createAndSaveEvent(now, study, "이벤트");
        Enrollment enrollment = Enrollment.builder()
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(false)
                .attended(false)
                .user(user)
                .build();
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        event.addEnrollment(savedEnrollment);

        // expected
        assertThatThrownBy(() -> eventService.acceptEnrollment(guest.getId(), event.getId(), enrollment.getId(), path))
                .isInstanceOf(InvalidRecruitmentStateException.class);
    }

    @Test
    @DisplayName("이벤트 이미 승인된 상태에서 승인 요청시 예외 발생")
    void acceptEnrollment_after_accepted() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);
        study.startRecruit(now);
        // 스터디 세팅
        User guest = createAndSaveUser("guest", Role.GUEST);
        StudyMember studyMember = StudyMember.createMember(guest, study);
        StudyMember savedMember = studyMemberRepository.save(studyMember);

        Event event = createAndSaveEvent(now, study, "이벤트");
        Enrollment enrollment = Enrollment.builder()
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(true)
                .attended(false)
                .user(guest)
                .build();
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        event.addEnrollment(savedEnrollment);

        // expected
        assertThatThrownBy(() -> eventService.acceptEnrollment(user.getId(), event.getId(), enrollment.getId(), path))
                .isInstanceOf(InvalidEnrollmentStateException.class);
    }

    @Test
    @DisplayName("이벤트 이미 참석 중인 상태에서 승인 요청시 예외 발생")
    void acceptEnrollment_after_attended() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);
        study.startRecruit(now);
        // 스터디 세팅
        User guest = createAndSaveUser("guest", Role.GUEST);
        StudyMember studyMember = StudyMember.createMember(guest, study);
        StudyMember savedMember = studyMemberRepository.save(studyMember);

        Event event = createAndSaveEvent(now, study, "이벤트");
        Enrollment enrollment = Enrollment.builder()
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(true)
                .attended(true)
                .user(guest)
                .build();
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        event.addEnrollment(savedEnrollment);

        // expected
        assertThatThrownBy(() -> eventService.acceptEnrollment(user.getId(), event.getId(), enrollment.getId(), path))
                .isInstanceOf(InvalidEnrollmentStateException.class);
    }

    @Test
    @DisplayName("모임 참가 거절 요청 시 참가 정보(enrollment)의 승인 상태(accepted)를 false로 유지한다")
    void rejectEnrollment() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);
        study.startRecruit(now);
        // 스터디 세팅
        User guest = createAndSaveUser("guest", Role.GUEST);
        StudyMember studyMember = StudyMember.createMember(guest, study);
        StudyMember savedMember = studyMemberRepository.save(studyMember);

        Event event = createAndSaveEvent(now, study, "이벤트");
        Enrollment enrollment = Enrollment.builder()
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(true)
                .attended(false)
                .user(guest)
                .build();
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        event.addEnrollment(savedEnrollment);

        // when
        EnrollmentResponse response = eventService.rejectEnrollment(user.getId(), event.getId(), savedEnrollment.getId(), path);

        // then
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getEnrollmentId()).isNotNull();
        Assertions.assertThat(response.getEnrolledAt()).isEqualTo(now.plusMinutes(10));
        Assertions.assertThat(response.isAccepted()).isFalse();
        Assertions.assertThat(response.isAttended()).isFalse();
    }
    /*
      reject 이 안되는 경우는?
      1. unpublished / closed / recruiting 아닐떄
      2. userId, eventId, path
      3. enrollmentId
     */

    @Test
    @DisplayName("공개(publish)되지 않은 스터디의 모임 참가거절(rejectEnrollment) 시도 시 예외 발생")
    void rejectEnrollment_with_unpublished_study() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        // 스터디 세팅
        User guest = createAndSaveUser("guest", Role.GUEST);
        StudyMember studyMember = StudyMember.createMember(guest, study);
        StudyMember savedMember = studyMemberRepository.save(studyMember);

        Event event = createAndSaveEvent(now, study, "이벤트");
        Enrollment enrollment = Enrollment.builder()
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(true)
                .attended(false)
                .user(guest)
                .build();
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        event.addEnrollment(savedEnrollment);

        // expected
        assertThatThrownBy(() -> eventService.rejectEnrollment(user.getId(), event.getId(), enrollment.getId(), path))
                .isInstanceOf(InvalidStudyPublishStateException.class);
    }

    @Test
    @DisplayName("이미 종료된(closed) 스터디의 모임 참가거절(rejectEnrollment) 시도 시 예외 발생")
    void rejectEnrollment_with_closed_study() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);
        study.close(clock);
        // 스터디 세팅
        User guest = createAndSaveUser("guest", Role.GUEST);
        StudyMember studyMember = StudyMember.createMember(guest, study);
        StudyMember savedMember = studyMemberRepository.save(studyMember);

        Event event = createAndSaveEvent(now, study, "이벤트");
        Enrollment enrollment = Enrollment.builder()
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(true)
                .attended(false)
                .user(guest)
                .build();
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        event.addEnrollment(savedEnrollment);

        // expected
        assertThatThrownBy(() -> eventService.rejectEnrollment(user.getId(), event.getId(), enrollment.getId(), path))
                .isInstanceOf(InvalidStudyPublishStateException.class);
    }

    @Test
    @DisplayName("모집(recruiting)중이 아닌 스터디의 모임 참가거절(rejectEnrollment) 시도 시 예외 발생")
    void rejectEnrollment_before_recruiting() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);
        // 스터디 세팅
        User guest = createAndSaveUser("guest", Role.GUEST);
        StudyMember studyMember = StudyMember.createMember(guest, study);
        StudyMember savedMember = studyMemberRepository.save(studyMember);

        Event event = createAndSaveEvent(now, study, "이벤트");
        Enrollment enrollment = Enrollment.builder()
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(true)
                .attended(false)
                .user(guest)
                .build();
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        event.addEnrollment(savedEnrollment);

        // expected
        assertThatThrownBy(() -> eventService.rejectEnrollment(user.getId(), event.getId(), enrollment.getId(), path))
                .isInstanceOf(InvalidRecruitmentStateException.class);
    }

    @Test
    @DisplayName("잘못된 userId로 스터디의 모임 참가거절(rejectEnrollment) 시도 시 예외 발생")
    void rejectEnrollment_with_wrong_userId() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);
        study.startRecruit(now);
        // 스터디 세팅
        User guest = createAndSaveUser("guest", Role.GUEST);
        StudyMember studyMember = StudyMember.createMember(guest, study);
        StudyMember savedMember = studyMemberRepository.save(studyMember);

        Event event = createAndSaveEvent(now, study, "이벤트");
        Enrollment enrollment = Enrollment.builder()
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(true)
                .attended(false)
                .user(guest)
                .build();
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        event.addEnrollment(savedEnrollment);

        // expected
        assertThatThrownBy(() -> eventService.rejectEnrollment(INVALID_USER_ID, event.getId(), enrollment.getId(), path))
                .isInstanceOf(EventNotFoundException.class);
    }

    @Test
    @DisplayName("잘못된 eventId로 스터디의 모임 참가거절(rejectEnrollment) 시도 시 예외 발생")
    void rejectEnrollment_with_wrong_eventId() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);
        study.startRecruit(now);
        // 스터디 세팅
        User guest = createAndSaveUser("guest", Role.GUEST);
        StudyMember studyMember = StudyMember.createMember(guest, study);
        StudyMember savedMember = studyMemberRepository.save(studyMember);

        Event event = createAndSaveEvent(now, study, "이벤트");
        Enrollment enrollment = Enrollment.builder()
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(true)
                .attended(false)
                .user(guest)
                .build();
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        event.addEnrollment(savedEnrollment);

        // expected
        assertThatThrownBy(() -> eventService.rejectEnrollment(user.getId(), INVALID_USER_ID, enrollment.getId(), path))
                .isInstanceOf(EventNotFoundException.class);
    }

    @Test
    @DisplayName("잘못된 path로 스터디의 모임 참가거절(rejectEnrollment) 시도 시 예외 발생")
    void rejectEnrollment_with_wrong_path() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);
        study.startRecruit(now);
        // 스터디 세팅
        User guest = createAndSaveUser("guest", Role.GUEST);
        StudyMember studyMember = StudyMember.createMember(guest, study);
        StudyMember savedMember = studyMemberRepository.save(studyMember);

        Event event = createAndSaveEvent(now, study, "이벤트");
        Enrollment enrollment = Enrollment.builder()
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(true)
                .attended(false)
                .user(guest)
                .build();
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        event.addEnrollment(savedEnrollment);

        // expected
        assertThatThrownBy(() -> eventService.rejectEnrollment(user.getId(), event.getId(), enrollment.getId(), "wrong"))
                .isInstanceOf(StudyNotFoundException.class);
    }

    @Test
    @DisplayName("잘못된 enrollmentId로 스터디의 모임 참가거절(rejectEnrollment) 시도 시 예외 발생")
    void rejectEnrollment_with_wrong_enrollmentId() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);
        study.startRecruit(now);
        // 스터디 세팅
        User guest = createAndSaveUser("guest", Role.GUEST);
        StudyMember studyMember = StudyMember.createMember(guest, study);
        StudyMember savedMember = studyMemberRepository.save(studyMember);

        Event event = createAndSaveEvent(now, study, "이벤트");
        Enrollment enrollment = Enrollment.builder()
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(true)
                .attended(false)
                .user(guest)
                .build();
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        event.addEnrollment(savedEnrollment);

        // expected
        assertThatThrownBy(() -> eventService.rejectEnrollment(user.getId(), event.getId(), INVALID_USER_ID, path))
                .isInstanceOf(InvalidEnrollmentException.class);
    }

    @Test
    @DisplayName("모임 체크인 요청 시 참석 상태(attended)를 true로 변경한다")
    void checkInEnrollment() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User manager = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, manager);
        study.publish(clock);
        study.startRecruit(now);
        // 스터디멤버 세팅
        User guest = createAndSaveUser("guest", Role.GUEST);
        StudyMember studyMember = StudyMember.createMember(guest, study);
        StudyMember savedMember = studyMemberRepository.save(studyMember);

        Event event = createAndSaveEvent(now, study, "이벤트");
        Enrollment enrollment = Enrollment.builder()
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(true)
                .attended(false)
                .user(guest)
                .build();
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        event.addEnrollment(savedEnrollment);

        // when
        EnrollmentResponse response = eventService.checkInEnrollment(guest.getId(), event.getId(), savedEnrollment.getId(), path);

        // then
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getEnrollmentId()).isNotNull();
        Assertions.assertThat(response.getEnrolledAt()).isEqualTo(now.plusMinutes(10));
        Assertions.assertThat(response.isAccepted()).isTrue();
        Assertions.assertThat(response.isAttended()).isTrue();
    }


    //////////////////////////////////////////////////
        /*
      checkIn 이 안되는 경우는?
      1. unpublished / closed / recruiting 아닐떄
      2. userId, eventId, path
      3. enrollmentId
     */

    @Test
    @DisplayName("공개(publish)되지 않은 스터디의 모임 체크인(checkInEnrollment) 시도 시 예외 발생")
    void checkInEnrollment_with_unpublished_study() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        // 스터디 세팅
        User guest = createAndSaveUser("guest", Role.GUEST);
        StudyMember studyMember = StudyMember.createMember(guest, study);
        StudyMember savedMember = studyMemberRepository.save(studyMember);

        Event event = createAndSaveEvent(now, study, "이벤트");
        Enrollment enrollment = Enrollment.builder()
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(true)
                .attended(false)
                .user(user)
                .build();
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        event.addEnrollment(savedEnrollment);

        // expected
        assertThatThrownBy(() -> eventService.checkInEnrollment(guest.getId(), event.getId(), enrollment.getId(), path))
                .isInstanceOf(InvalidStudyPublishStateException.class);
    }

    @Test
    @DisplayName("이미 종료된(closed) 스터디의 모임 체크인(checkInEnrollment) 시도 시 예외 발생")
    void checkInEnrollment_with_closed_study() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);
        study.close(clock);
        // 스터디 세팅
        User guest = createAndSaveUser("guest", Role.GUEST);
        StudyMember studyMember = StudyMember.createMember(guest, study);
        StudyMember savedMember = studyMemberRepository.save(studyMember);

        Event event = createAndSaveEvent(now, study, "이벤트");
        Enrollment enrollment = Enrollment.builder()
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(true)
                .attended(false)
                .user(user)
                .build();
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        event.addEnrollment(savedEnrollment);

        // expected
        assertThatThrownBy(() -> eventService.checkInEnrollment(guest.getId(), event.getId(), enrollment.getId(), path))
                .isInstanceOf(InvalidStudyPublishStateException.class);
    }

    @Test
    @DisplayName("모집(recruiting)중이 아닌 스터디의 모임 체크인(checkInEnrollment) 시도 시 예외 발생")
    void checkInEnrollment_before_recruiting() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);
        // 스터디 세팅
        User guest = createAndSaveUser("guest", Role.GUEST);
        StudyMember studyMember = StudyMember.createMember(guest, study);
        StudyMember savedMember = studyMemberRepository.save(studyMember);

        Event event = createAndSaveEvent(now, study, "이벤트");
        Enrollment enrollment = Enrollment.builder()
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(true)
                .attended(false)
                .user(user)
                .build();
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        event.addEnrollment(savedEnrollment);

        // expected
        assertThatThrownBy(() -> eventService.checkInEnrollment(guest.getId(), event.getId(), enrollment.getId(), path))
                .isInstanceOf(InvalidRecruitmentStateException.class);
    }

    @Test
    @DisplayName("잘못된 userId로 스터디의 모임 체크인(checkInEnrollment) 시도 시 예외 발생")
    void checkInEnrollment_with_wrong_userId() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);
        study.startRecruit(now);
        // 스터디 세팅
        User guest = createAndSaveUser("guest", Role.GUEST);
        StudyMember studyMember = StudyMember.createMember(guest, study);
        StudyMember savedMember = studyMemberRepository.save(studyMember);

        Event event = createAndSaveEvent(now, study, "이벤트");
        Enrollment enrollment = Enrollment.builder()
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(true)
                .attended(false)
                .user(user)
                .build();
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        event.addEnrollment(savedEnrollment);

        // expected
        assertThatThrownBy(() -> eventService.checkInEnrollment(INVALID_USER_ID, event.getId(), enrollment.getId(), path))
                .isInstanceOf(EventNotFoundException.class);
    }

    @Test
    @DisplayName("잘못된 eventId로 스터디의 모임 체크인(checkInEnrollment) 시도 시 예외 발생")
    void checkInEnrollment_with_wrong_eventId() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);
        study.startRecruit(now);
        // 스터디 세팅
        User guest = createAndSaveUser("guest", Role.GUEST);
        StudyMember studyMember = StudyMember.createMember(guest, study);
        StudyMember savedMember = studyMemberRepository.save(studyMember);

        Event event = createAndSaveEvent(now, study, "이벤트");
        Enrollment enrollment = Enrollment.builder()
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(true)
                .attended(false)
                .user(user)
                .build();
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        event.addEnrollment(savedEnrollment);

        // expected
        assertThatThrownBy(() -> eventService.checkInEnrollment(guest.getId(), INVALID_USER_ID, enrollment.getId(), path))
                .isInstanceOf(EventNotFoundException.class);
    }

    @Test
    @DisplayName("잘못된 path로 스터디의 모임 체크인(checkInEnrollment) 시도 시 예외 발생")
    void checkInEnrollment_with_wrong_path() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);
        study.startRecruit(now);
        // 스터디 세팅
        User guest = createAndSaveUser("guest", Role.GUEST);
        StudyMember studyMember = StudyMember.createMember(guest, study);
        StudyMember savedMember = studyMemberRepository.save(studyMember);

        Event event = createAndSaveEvent(now, study, "이벤트");
        Enrollment enrollment = Enrollment.builder()
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(true)
                .attended(false)
                .user(user)
                .build();
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        event.addEnrollment(savedEnrollment);

        // expected
        assertThatThrownBy(() -> eventService.checkInEnrollment(guest.getId(), event.getId(), enrollment.getId(), "wrong"))
                .isInstanceOf(StudyNotFoundException.class);
    }

    @Test
    @DisplayName("잘못된 enrollmentId로 스터디의 모임 체크인(checkInEnrollment) 시도 시 예외 발생")
    void checkInEnrollment_with_wrong_enrollmentId() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);
        study.startRecruit(now);
        // 스터디 세팅
        User guest = createAndSaveUser("guest", Role.GUEST);
        StudyMember studyMember = StudyMember.createMember(guest, study);
        StudyMember savedMember = studyMemberRepository.save(studyMember);

        Event event = createAndSaveEvent(now, study, "이벤트");
        Enrollment enrollment = Enrollment.builder()
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(true)
                .attended(false)
                .user(user)
                .build();
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        event.addEnrollment(savedEnrollment);

        // expected
        assertThatThrownBy(() -> eventService.checkInEnrollment(guest.getId(), event.getId(), INVALID_USER_ID, path))
                .isInstanceOf(InvalidEnrollmentException.class);
    }


    @Test
    @DisplayName("모임 체크인 취소 요청 시 참석 상태(attended)를 false로 변경한다")
    void cancelCheckInEnrollment() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);
        study.startRecruit(now);
        // 스터디 멤버 세팅
        User guest = createAndSaveUser("guest", Role.GUEST);
        StudyMember studyMember = StudyMember.createMember(guest, study);
        StudyMember savedMember = studyMemberRepository.save(studyMember);

        Event event = createAndSaveEvent(now, study, "이벤트");
        Enrollment enrollment = Enrollment.builder()
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(true)
                .attended(true)
                .user(guest)
                .build();
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        event.addEnrollment(savedEnrollment);

        // when
        EnrollmentResponse response = eventService.cancelCheckInEnrollment(guest.getId(), event.getId(), savedEnrollment.getId(), path);

        // then
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getEnrollmentId()).isNotNull();
        Assertions.assertThat(response.getEnrolledAt()).isEqualTo(now.plusMinutes(10));
        Assertions.assertThat(response.isAccepted()).isTrue();
        Assertions.assertThat(response.isAttended()).isFalse();
    }

    @Test
    @DisplayName("공개(publish)되지 않은 스터디의 모임 체크인 취소(cancelCheckInEnrollment) 시도 시 예외 발생")
    void cancelCheckInEnrollment_with_unpublished_study() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        // 스터디 세팅
        User guest = createAndSaveUser("guest", Role.GUEST);
        StudyMember studyMember = StudyMember.createMember(guest, study);
        StudyMember savedMember = studyMemberRepository.save(studyMember);

        Event event = createAndSaveEvent(now, study, "이벤트");
        Enrollment enrollment = Enrollment.builder()
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(true)
                .attended(true)
                .user(user)
                .build();
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        event.addEnrollment(savedEnrollment);

        // expected
        assertThatThrownBy(() -> eventService.cancelCheckInEnrollment(guest.getId(), event.getId(), enrollment.getId(), path))
                .isInstanceOf(InvalidStudyPublishStateException.class);
    }

    @Test
    @DisplayName("이미 종료된(closed) 스터디의 모임 체크인 취소(cancelCheckInEnrollment) 시도 시 예외 발생")
    void cancelCheckInEnrollment_with_closed_study() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);
        study.close(clock);
        // 스터디 세팅
        User guest = createAndSaveUser("guest", Role.GUEST);
        StudyMember studyMember = StudyMember.createMember(guest, study);
        StudyMember savedMember = studyMemberRepository.save(studyMember);

        Event event = createAndSaveEvent(now, study, "이벤트");
        Enrollment enrollment = Enrollment.builder()
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(true)
                .attended(true)
                .user(user)
                .build();
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        event.addEnrollment(savedEnrollment);

        // expected
        assertThatThrownBy(() -> eventService.cancelCheckInEnrollment(guest.getId(), event.getId(), enrollment.getId(), path))
                .isInstanceOf(InvalidStudyPublishStateException.class);
    }

    @Test
    @DisplayName("모집(recruiting)중이 아닌 스터디의 모임 체크인 취소(cancelCheckInEnrollment) 시도 시 예외 발생")
    void cancelCheckInEnrollment_before_recruiting() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);
        // 스터디 세팅
        User guest = createAndSaveUser("guest", Role.GUEST);
        StudyMember studyMember = StudyMember.createMember(guest, study);
        StudyMember savedMember = studyMemberRepository.save(studyMember);

        Event event = createAndSaveEvent(now, study, "이벤트");
        Enrollment enrollment = Enrollment.builder()
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(true)
                .attended(true)
                .user(user)
                .build();
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        event.addEnrollment(savedEnrollment);

        // expected
        assertThatThrownBy(() -> eventService.cancelCheckInEnrollment(guest.getId(), event.getId(), enrollment.getId(), path))
                .isInstanceOf(InvalidRecruitmentStateException.class);
    }

    @Test
    @DisplayName("잘못된 userId로 스터디의 모임 체크인 취소(cancelCheckInEnrollment) 시도 시 예외 발생")
    void cancelCheckInEnrollment_with_wrong_userId() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);
        study.startRecruit(now);
        // 스터디 세팅
        User guest = createAndSaveUser("guest", Role.GUEST);
        StudyMember studyMember = StudyMember.createMember(guest, study);
        StudyMember savedMember = studyMemberRepository.save(studyMember);

        Event event = createAndSaveEvent(now, study, "이벤트");
        Enrollment enrollment = Enrollment.builder()
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(true)
                .attended(true)
                .user(user)
                .build();
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        event.addEnrollment(savedEnrollment);

        // expected
        assertThatThrownBy(() -> eventService.cancelCheckInEnrollment(INVALID_USER_ID, event.getId(), enrollment.getId(), path))
                .isInstanceOf(EventNotFoundException.class);
    }

    @Test
    @DisplayName("잘못된 eventId로 스터디의 모임 체크인 취소(cancelCheckInEnrollment) 시도 시 예외 발생")
    void cancelCheckInEnrollment_with_wrong_eventId() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);
        study.startRecruit(now);
        // 스터디 세팅
        User guest = createAndSaveUser("guest", Role.GUEST);
        StudyMember studyMember = StudyMember.createMember(guest, study);
        StudyMember savedMember = studyMemberRepository.save(studyMember);

        Event event = createAndSaveEvent(now, study, "이벤트");
        Enrollment enrollment = Enrollment.builder()
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(true)
                .attended(true)
                .user(user)
                .build();
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        event.addEnrollment(savedEnrollment);

        // expected
        assertThatThrownBy(() -> eventService.cancelCheckInEnrollment(guest.getId(), INVALID_USER_ID, enrollment.getId(), path))
                .isInstanceOf(EventNotFoundException.class);
    }

    @Test
    @DisplayName("잘못된 path로 스터디의 모임 체크인 취소(cancelCheckInEnrollment) 시도 시 예외 발생")
    void cancelCheckInEnrollment_with_wrong_path() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);
        study.startRecruit(now);
        // 스터디 세팅
        User guest = createAndSaveUser("guest", Role.GUEST);
        StudyMember studyMember = StudyMember.createMember(guest, study);
        StudyMember savedMember = studyMemberRepository.save(studyMember);

        Event event = createAndSaveEvent(now, study, "이벤트");
        Enrollment enrollment = Enrollment.builder()
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(true)
                .attended(true)
                .user(user)
                .build();
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        event.addEnrollment(savedEnrollment);

        // expected
        assertThatThrownBy(() -> eventService.cancelCheckInEnrollment(guest.getId(), event.getId(), enrollment.getId(), "wrong"))
                .isInstanceOf(StudyNotFoundException.class);
    }

    @Test
    @DisplayName("잘못된 enrollmentId로 스터디의 모임 체크인 취소(cancelCheckInEnrollment) 시도 시 예외 발생")
    void cancelCheckInEnrollment_with_wrong_enrollmentId() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);
        study.startRecruit(now);
        // 스터디 세팅
        User guest = createAndSaveUser("guest", Role.GUEST);
        StudyMember studyMember = StudyMember.createMember(guest, study);
        StudyMember savedMember = studyMemberRepository.save(studyMember);

        Event event = createAndSaveEvent(now, study, "이벤트");
        Enrollment enrollment = Enrollment.builder()
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(true)
                .attended(true)
                .user(user)
                .build();
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        event.addEnrollment(savedEnrollment);

        // expected
        assertThatThrownBy(() -> eventService.cancelCheckInEnrollment(guest.getId(), event.getId(), INVALID_USER_ID, path))
                .isInstanceOf(InvalidEnrollmentException.class);
    }

    @Test
    @DisplayName("체크인(attend) 되지 않은 상태에서 스터디의 모임 체크인 취소(cancelCheckInEnrollment) 시도 시 예외 발생")
    void cancelCheckInEnrollment_before_attended() {
        // given
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = createAndSaveUser("admin", Role.ADMIN);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);
        study.startRecruit(now);
        // 스터디 세팅
        User guest = createAndSaveUser("guest", Role.GUEST);
        StudyMember studyMember = StudyMember.createMember(guest, study);
        StudyMember savedMember = studyMemberRepository.save(studyMember);

        Event event = createAndSaveEvent(now, study, "이벤트");
        Enrollment enrollment = Enrollment.builder()
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(true)
                .attended(false)
                .user(guest)
                .build();
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        event.addEnrollment(savedEnrollment);

        // expected
        assertThatThrownBy(() -> eventService.cancelCheckInEnrollment(guest.getId(), event.getId(), enrollment.getId(), path))
                .isInstanceOf(InvalidEnrollmentStateException.class);
    }

    private User createAndSaveUser(String name, Role role) {
        User user = User.builder()
                .name(name + "testName")
                .email(name + "test@gmail.com")
                .role(role)
                .build();

        return userRepository.save(user);
    }

    private Study createAndSaveStudy(String path, User user) {
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        Study savedStudy = studyRepository.save(study);

        StudyManager manager = StudyManager.createManager(user, savedStudy);
        StudyManager savedManager = studyManagerRepository.save(manager);
        savedStudy.addManager(savedManager);

        return savedStudy;
    }

    private Event createAndSaveEvent(LocalDateTime now, Study study, String alias) {
        Event event = Event.builder()
                .title("제목" + alias)
                .description("소개" + alias)
                .endEnrollmentDateTime(now.plusMinutes(1))
                .startDateTime(now.plusMinutes(2))
                .endDateTime(now.plusMinutes(3))
                .limitOfEnrollments(2)
                .study(study)
                .build();

        return eventRepository.save(event);
    }

    private Event createAndSaveOldEvent(LocalDateTime now, Study study, String alias) {
        Event event = Event.builder()
                .title("제목" + alias)
                .description("소개" + alias)
                .endEnrollmentDateTime(now.plusMinutes(-30))
                .startDateTime(now.plusMinutes(-20))
                .endDateTime(now.plusMinutes(-10))
                .limitOfEnrollments(2)
                .study(study)
                .build();

        return eventRepository.save(event);
    }
}