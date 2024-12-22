package com.app.domain.event.service;

import com.app.domain.event.Enrollment;
import com.app.domain.event.Event;
import com.app.domain.event.EventEditor;
import com.app.domain.event.dto.*;
import com.app.domain.event.eventListener.event.EnrollmentAcceptedEvent;
import com.app.domain.event.eventListener.event.EnrollmentRejectedEvent;
import com.app.domain.event.repository.EnrollmentRepository;
import com.app.domain.event.repository.EventRepository;
import com.app.domain.study.Study;
import com.app.domain.study.eventListener.StudyUpdatedEvent;
import com.app.domain.study.service.StudyService;
import com.app.domain.user.User;
import com.app.domain.user.service.UserService;
import com.app.global.error.exception.EnrollmentAlreadyExistException;
import com.app.global.error.exception.EventNotFoundException;
import com.app.global.error.exception.InvalidEnrollmentException;
import com.app.global.error.exception.InvalidEnrollmentStateException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    // TODO : event 발행
    private final EventRepository eventRepository;
    private final StudyService studyService;
    private final ApplicationEventPublisher eventPublisher;
    private final EnrollmentRepository enrollmentRepository;
    private final UserService userService;
    private final Clock clock;

    public EventCreateResponse createEvent(Long userId, String path, EventCreateRequest request) {
        Study study = studyService.validateStudyIsActive(path);
        Event event = createEvent(request, study);
        Event savedEvent = eventRepository.save(event);

        eventPublisher.publishEvent(new StudyUpdatedEvent(event.getStudy(),
                "'" + event.getTitle() + "' 모임을 만들었습니다."));

        return EventCreateResponse.of(savedEvent);
    }

    private Event createEvent(EventCreateRequest request, Study study) {
        return Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .endEnrollmentDateTime(request.getEndEnrollmentDateTime())
                .startDateTime(request.getStartDateTime())
                .endDateTime(request.getEndDateTime())
                .limitOfEnrollments(request.getLimitOfEnrollments())
                .study(study)
                .build();
    }

    public EventResponse getEvent(Long eventId) {
        Event event = eventRepository.findEventWithEnrollmentById(eventId).orElseThrow(EventNotFoundException::new);

        return EventResponse.of(event);
    }

    public EventsResponse getEvents(String path) {
        Study study = studyService.findByPath(path); // path 체크용
        List<Event> events = eventRepository.findAllEventWithEnrollmentByPath(path);
        List<Event> newEvents = new ArrayList<>();
        List<Event> oldEvents = new ArrayList<>();

        events.forEach(event -> {
            if (event.getEndDateTime().isBefore(LocalDateTime.now(clock))) {
                oldEvents.add(event);
            } else {
                newEvents.add(event);
            }
        });

        return EventsResponse.builder()
                .oldEvents(oldEvents.stream().map(EventResponse::of).toList())
                .newEvents(newEvents.stream().map(EventResponse::of).toList())
                .build();
    }

    public EventUpdateResponse updateEvent(Long userId, Long eventId, String path, EventUpdateRequest request) {
        Study study = studyService.validateStudyIsActive(path);
        Event event = eventRepository.findEventByIdIfAuthorized(userId, eventId, path).orElseThrow(EventNotFoundException::new);

        EventEditor eventEditor = event.toEditor()
                .title(request.getTitle())
                .description(request.getDescription())
                .endEnrollmentDateTime(request.getEndEnrollmentDateTime())
                .startDateTime(request.getStartDateTime())
                .endDateTime(request.getEndDateTime())
                .limitOfEnrollments(request.getLimitOfEnrollments())
                .build();
        event.edit(eventEditor);

        eventPublisher.publishEvent(new StudyUpdatedEvent(event.getStudy(),
                "'" + event.getTitle() + "' 모임 정보를 수정했으니 확인하세요."));

        return EventUpdateResponse.of(event);
    }

    public void deleteEvent(Long userId, Long eventId, String path) {
        // 해당 이벤트가 존재하고 권한이 있는지 체크용
        Event event = eventRepository.findEventByIdIfAuthorized(userId, eventId, path).orElseThrow(EventNotFoundException::new);
        eventRepository.deleteById(eventId);

        eventPublisher.publishEvent(new StudyUpdatedEvent(event.getStudy(),
                "'" + event.getTitle() + "' 모임을 취소했습니다."));
    }

    public EnrollmentCreateResponse createEnrollment(Long userId, Long eventId, String path) {
        User user = userService.getById(userId);
        Study study = studyService.validateStudyIsRecruiting(path);
        Event event = eventRepository.findEventWithEnrollmentById(eventId).orElseThrow(EventNotFoundException::new);

        if (enrollmentRepository.existsByEventAndUser(event, user)) {
            throw new EnrollmentAlreadyExistException();
        }
        Enrollment enrollment = Enrollment.builder()
                .event(event)
                .enrolledAt(LocalDateTime.now(clock))
                .accepted(event.isAbleToAccept())
                .user(user)
                .build();
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        return EnrollmentCreateResponse.of(savedEnrollment);
    }

    public void cancelEnrollment(Long userId, Long eventId, String path) {
        User user = userService.getById(userId);
        Study study = studyService.validateStudyIsRecruiting(path);
        Event event = eventRepository.findEventWithEnrollmentById(eventId).orElseThrow(EventNotFoundException::new);
        Enrollment enrollment = enrollmentRepository.findByEventAndUser(event, user).orElseThrow(InvalidEnrollmentException::new);

        if (enrollment.isAccepted() && !enrollment.isAttended()) {
            event.removeEnrollment(enrollment);
            enrollmentRepository.delete(enrollment);
        } else {
            throw new InvalidEnrollmentStateException();
        }
    }

    public EnrollmentResponse acceptEnrollment(Long userId, Long eventId, Long enrollmentId, String path) {
        Study study = studyService.validateStudyIsRecruiting(path);
        Event event = eventRepository.findEventByIdIfAuthorized(userId, eventId, path).orElseThrow(EventNotFoundException::new);
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId).orElseThrow(InvalidEnrollmentException::new);
        event.accept(enrollment);

        eventPublisher.publishEvent(new EnrollmentAcceptedEvent(enrollment));

        return EnrollmentResponse.of(enrollment);
    }

    public EnrollmentResponse rejectEnrollment(Long userId, Long eventId, Long enrollmentId, String path) {
        Study study = studyService.validateStudyIsRecruiting(path);
        Event event = eventRepository.findEventByIdIfAuthorized(userId, eventId, path).orElseThrow(EventNotFoundException::new);
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId).orElseThrow(InvalidEnrollmentException::new);
        event.reject(enrollment);

        eventPublisher.publishEvent(new EnrollmentRejectedEvent(enrollment));

        return EnrollmentResponse.of(enrollment);
    }

    public EnrollmentResponse checkInEnrollment(Long userId, Long eventId, Long enrollmentId, String path) {
        Study study = studyService.validateStudyIsRecruiting(path);
        Event event = eventRepository.findEventByIdIfAuthorized(userId, eventId, path).orElseThrow(EventNotFoundException::new);
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId).orElseThrow(InvalidEnrollmentException::new);

        if (enrollment.isAccepted() && !enrollment.isAttended()) {
            enrollment.setAttended(true);
        } else {
            throw new InvalidEnrollmentStateException();
        }
        return EnrollmentResponse.of(enrollment);
    }

    public EnrollmentResponse cancelCheckInEnrollment(Long userId, Long eventId, Long enrollmentId, String path) {
        Study study = studyService.validateStudyIsRecruiting(path);
        Event event = eventRepository.findEventByIdIfAuthorized(userId, eventId, path).orElseThrow(EventNotFoundException::new);
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId).orElseThrow(InvalidEnrollmentException::new);

        if (enrollment.isAccepted() && enrollment.isAttended()) {
            enrollment.setAttended(false);
        } else {
            throw new InvalidEnrollmentStateException();
        }
        return EnrollmentResponse.of(enrollment);
    }
}

