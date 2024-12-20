package com.app.domain.event.controller;

import com.app.TestUtils;
import com.app.WithAccount;
import com.app.domain.event.Enrollment;
import com.app.domain.event.Event;
import com.app.domain.event.dto.EventCreateRequest;
import com.app.domain.event.dto.EventUpdateRequest;
import com.app.domain.event.repository.EnrollmentRepository;
import com.app.domain.event.repository.EventRepository;
import com.app.domain.study.Study;
import com.app.domain.study.repository.StudyRepository;
import com.app.domain.study.studyManager.StudyManager;
import com.app.domain.study.studyManager.repository.StudyManagerRepository;
import com.app.domain.user.User;
import com.app.domain.user.repository.UserRepository;
import com.app.global.error.exception.UserNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static com.app.TestUtils.getAuthenticatedEmail;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class EventControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    StudyRepository studyRepository;

    @Autowired
    StudyManagerRepository studyManagerRepository;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Test
    @WithAccount
    @DisplayName("이벤트 생성 요청 시 올바른 입력 값을 받으면 이벤트 생성에 성공한다")
    void createEvent_with_correct_input() throws Exception {
        // given
        Clock clock = TestUtils.getFixedClock();
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = userRepository.findByEmail(getAuthenticatedEmail()).orElseThrow(UserNotFoundException::new);
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

        String json = objectMapper.writeValueAsString(request);
        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/study/{path}/events/new", path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("이벤트1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("설명1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.startDateTime").value(Matchers.startsWith(now.plusMinutes(2).toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.endDateTime").value(Matchers.startsWith(now.plusMinutes(3).toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.limitOfEnrollments").value(2))
                .andDo(MockMvcResultHandlers.print());
    }

    @ParameterizedTest
    @CsvSource({
            "0, 2, 3",   // Case 1: endEnrollmentDateTime 가 과거인 경우
            "1, -1, 3",          // Case 2: startDateTime 이 모집마감 전인 경우
            "1, 2, 1"              // Case 3: endDateTime 이 이벤트 시작시간 전인 경우
    })
    @WithAccount
    @DisplayName("이벤트 생성 요청 시 모집마감, 이벤트 시작, 종료 시간이 올바르지 않으면 예외 발생")
    void createEvent_with_wrong_time_input(int endEnrollmentOffset, int startOffset, int endOffset) throws Exception {
        // given
        Clock clock = TestUtils.getFixedClock();
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = userRepository.findByEmail(getAuthenticatedEmail()).orElseThrow(UserNotFoundException::new);
        Study study = createAndSaveStudy(path, user);

        EventCreateRequest request = EventCreateRequest.builder()
                .title("이벤트1")
                .description("설명1")
                .endEnrollmentDateTime(now.plusMinutes(endEnrollmentOffset))
                .startDateTime(now.plusMinutes(startOffset))
                .endDateTime(now.plusMinutes(endOffset))
                .limitOfEnrollments(2)
                .build();

        String json = objectMapper.writeValueAsString(request);
        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/study/{path}/events/new", path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("특정 이벤트 ID로 조회 요청 시 해당 이벤트 정보를 반환한다")
    void getEvent() throws Exception {
        // given
        Clock clock = TestUtils.getFixedClock();
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = userRepository.findByEmail(getAuthenticatedEmail()).orElseThrow(UserNotFoundException::new);
        Study study = createAndSaveStudy(path, user);
        Event event = createAndSaveEvent(now, study, "1");

        // expected
        mockMvc.perform(MockMvcRequestBuilders.get("/study/{path}/events/{eventId}", path, event.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("스터디 경로로 이벤트 목록 조회 요청 시 새 이벤트와 지난 이벤트를 분류하여 반환한다")
    void getEvents() throws Exception {
        //given
        Clock clock = TestUtils.getFixedClock();
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = userRepository.findByEmail(getAuthenticatedEmail()).orElseThrow(UserNotFoundException::new);
        Study study = createAndSaveStudy(path, user);
        List<Event> list = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Event event = createAndSaveEvent(now, study, "new" + i);
            Event oldEvent = createAndSaveOldEvent(now, study, "old" + i);
            list.add(event);
            list.add(oldEvent);
        }
        eventRepository.saveAll(list);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.get("/study/{path}/events", path))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.newEvents[0].title").value("이벤트new1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.newEvents[0].description").value("이벤트소개new1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.newEvents[0].limitOfEnrollments").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.oldEvents[0].title").value("이벤트old1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.oldEvents[0].description").value("이벤트소개old1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.oldEvents[0].limitOfEnrollments").value(2))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("이벤트 수정 요청 시 올바른 입력 값을 받으면 이벤트 수정에 성공한다")
    void updateEvent() throws Exception {
        // given
        Clock clock = TestUtils.getFixedClock();
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = userRepository.findByEmail(getAuthenticatedEmail()).orElseThrow(UserNotFoundException::new);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);

        Event event = createAndSaveEvent(now, study, "1");
        EventUpdateRequest request = EventUpdateRequest.builder()
                .title("수정제목")
                .description("수정소개")
                .endEnrollmentDateTime(now.plusMinutes(11))
                .startDateTime(now.plusMinutes(12))
                .endDateTime(now.plusMinutes(13))
                .limitOfEnrollments(3)
                .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.patch("/study/{path}/events/{eventId}", path, event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("title").value("수정제목"))
                .andExpect(MockMvcResultMatchers.jsonPath("description").value("수정소개"))
                .andExpect(MockMvcResultMatchers.jsonPath("endEnrollmentDateTime").value(now.plusMinutes(11).toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("startDateTime").value(now.plusMinutes(12).toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("endDateTime").value(now.plusMinutes(13).toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("limitOfEnrollments").value(3))
                .andDo(MockMvcResultHandlers.print());
    }

    @ParameterizedTest
    @CsvSource({
            "0, 2, 3",   // Case 1: endEnrollmentDateTime 가 과거인 경우
            "1, -1, 3",          // Case 2: startDateTime 이 모집마감 전인 경우
            "1, 2, 1"              // Case 3: endDateTime 이 이벤트 시작시간 전인 경우
    })
    @WithAccount
    @DisplayName("이벤트 수정 요청 시 모집마감, 이벤트 시작, 종료 시간이 올바르지 않으면 예외 발생")
    void updateEvent_with_wrong_input(int endEnrollmentOffset, int startOffset, int endOffset) throws Exception {
        // given
        Clock clock = TestUtils.getFixedClock();
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.MILLIS);

        String path = "test";
        User user = userRepository.findByEmail(getAuthenticatedEmail()).orElseThrow(UserNotFoundException::new);
        Study study = createAndSaveStudy(path, user);
        Event event = createAndSaveEvent(now, study, "1");

        EventUpdateRequest request = EventUpdateRequest.builder()
                .title("수정제목")
                .description("수정소개")
                .endEnrollmentDateTime(now.plusMinutes(endEnrollmentOffset))
                .startDateTime(now.plusMinutes(startOffset))
                .endDateTime(now.plusMinutes(endOffset))
                .limitOfEnrollments(3)
                .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.patch("/study/{path}/events/{eventId}", path, event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("이벤트 삭제 요청 시 해당 이벤트 삭제에 성공한다")
    void deleteEvent() throws Exception {
        // given
        Clock clock = TestUtils.getFixedClock();
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = userRepository.findByEmail(getAuthenticatedEmail()).orElseThrow(UserNotFoundException::new);
        Study study = createAndSaveStudy(path, user);
        Event event = createAndSaveEvent(now, study, "1");

        // expected
        mockMvc.perform(MockMvcRequestBuilders.delete("/study/{path}/events/{eventId}", path, event.getId()))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("모임 참가 요청 시 성공적으로 참가(enrollment)를 등록한다")
    void newEnrollment() throws Exception {
        // given
        Clock clock = TestUtils.getFixedClock();
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = userRepository.findByEmail(getAuthenticatedEmail()).orElseThrow(UserNotFoundException::new);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);
        study.startRecruit(now);

        Event event = createAndSaveEvent(now, study, "1");
        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/study/{path}/events/{eventId}/enroll", path, event.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

    }

    @Test
    @WithAccount
    @DisplayName("모임 참가 취소 요청 시 성공적으로 참가(enrollment)를 삭제한다")
    void cancelEnrollment() throws Exception {
        // given
        Clock clock = TestUtils.getFixedClock();
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = userRepository.findByEmail(getAuthenticatedEmail()).orElseThrow(UserNotFoundException::new);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);
        study.startRecruit(now);

        Event event = createAndSaveEvent(now, study, "1");
        Enrollment enrollment = Enrollment.builder()
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(event.isAbleToAccept())
                .user(user)
                .build();

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        event.addEnrollment(savedEnrollment);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.delete("/study/{path}/events/{eventId}/disenroll", path, event.getId()))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("참가 신청 허가 요청 시 참가자의 승인상태(accepted)를 true로 변경한다")
    void acceptEnrollment() throws Exception {
        // given
        Clock clock = TestUtils.getFixedClock();
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = userRepository.findByEmail(getAuthenticatedEmail()).orElseThrow(UserNotFoundException::new);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);
        study.startRecruit(now);

        Event event = createAndSaveEvent(now, study, "1");
        Enrollment enrollment = Enrollment.builder()
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(false)
                .user(user)
                .build();

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        event.addEnrollment(savedEnrollment);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/study/{path}/events/{eventId}/enrollments/{enrollmentId}/accept",
                        path, event.getId(), enrollment.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accepted").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.attended").value(false))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("참가 신청 거절 요청 시 참가자의 승인상태(accepted)를 false로 유지한다")
    void rejectEnrollment() throws Exception {
        // given
        Clock clock = TestUtils.getFixedClock();
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = userRepository.findByEmail(getAuthenticatedEmail()).orElseThrow(UserNotFoundException::new);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);
        study.startRecruit(now);

        Event event = createAndSaveEvent(now, study, "1");
        Enrollment enrollment = Enrollment.builder()
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(event.isAbleToAccept())
                .user(user)
                .build();

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        event.addEnrollment(savedEnrollment);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/study/{path}/events/{eventId}/enrollments/{enrollmentId}/reject",
                        path, event.getId(), enrollment.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accepted").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.attended").value(false))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("모임 참가 성공 시 참석 상태(attended)가 true로 변경된다")
    void checkInEnrollment() throws Exception {
        // given
        Clock clock = TestUtils.getFixedClock();
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = userRepository.findByEmail(getAuthenticatedEmail()).orElseThrow(UserNotFoundException::new);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);
        study.startRecruit(now);

        Event event = createAndSaveEvent(now, study, "1");
        Enrollment enrollment = Enrollment.builder()
                .event(event)
                .enrolledAt(now.plusMinutes(10))
                .accepted(true)
                .user(user)
                .build();

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        event.addEnrollment(savedEnrollment);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/study/{path}/events/{eventId}/enrollments/{enrollmentId}/check-in",
                        path, event.getId(), enrollment.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accepted").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.attended").value(true))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("모임 체크인 취소 요청 시 참석 상태(attended)를 false로 변경한다")
    void cancelCheckInEnrollment() throws Exception {
        // given
        Clock clock = TestUtils.getFixedClock();
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        String path = "test";
        User user = userRepository.findByEmail(getAuthenticatedEmail()).orElseThrow(UserNotFoundException::new);
        Study study = createAndSaveStudy(path, user);
        study.publish(clock);
        study.startRecruit(now);

        Event event = createAndSaveEvent(now, study, "1");
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
        mockMvc.perform(MockMvcRequestBuilders.post("/study/{path}/events/{eventId}/enrollments/{enrollmentId}/cancel-check-in",
                        path, event.getId(), enrollment.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accepted").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.attended").value(false))
                .andDo(MockMvcResultHandlers.print());
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
                .title("이벤트" + alias)
                .description("이벤트소개" + alias)
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
                .title("이벤트" + alias)
                .description("이벤트소개" + alias)
                .endEnrollmentDateTime(now.plusMinutes(-30))
                .startDateTime(now.plusMinutes(-20))
                .endDateTime(now.plusMinutes(-10))
                .limitOfEnrollments(2)
                .study(study)
                .build();

        return eventRepository.save(event);
    }
}