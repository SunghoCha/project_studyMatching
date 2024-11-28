package com.app.domain.event.service;

import com.app.domain.event.Enrollment;
import com.app.domain.event.Event;
import com.app.domain.event.EventEditor;
import com.app.domain.event.dto.*;
import com.app.domain.event.repository.EnrollmentRepository;
import com.app.domain.event.repository.EventRepository;
import com.app.domain.study.Study;
import com.app.domain.study.service.StudyService;
import com.app.domain.user.User;
import com.app.domain.user.service.UserService;
import com.app.global.error.exception.EnrollmentAlreadyExistException;
import com.app.global.error.exception.EventNotFoundException;
import com.app.global.error.exception.InvalidEnrollmentException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final StudyService studyService;
    private final ApplicationEventPublisher eventPublisher;
    private final EnrollmentRepository enrollmentRepository;
    private final UserService userService;

    public EventCreateResponse createEvent(Long userId, String path, EventCreateRequest request) {
        Study study = studyService.findStudyWithManager(userId, path);
        Event event = createEvent(request, study);
        // TODO : eventPublisher 로직 구현
        Event savedEvent = eventRepository.save(event);

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
        Event event = eventRepository.findById(eventId).orElseThrow(EventNotFoundException::new);

        return EventResponse.of(event);
    }

    public EventsResponse getEvents(String path) {
        List<Event> events = eventRepository.findAllByPath(path);
        List<Event> newEvents = new ArrayList<>();
        List<Event> oldEvents = new ArrayList<>();

        events.forEach(event -> {
            if (event.getEndDateTime().isBefore(LocalDateTime.now())) {
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
        // TODO : 모임 공개? 전에만 수정가능
        Event event = eventRepository.findEventByIdIfAuthorized(userId, eventId, path).orElseThrow(EventNotFoundException::new);

        EventEditor eventEditor = event.toEditor()
                .title(request.getTitle())
                .description(request.getDescription())
                .endErollmentDateTime(request.getEndEnrollmentDateTime())
                .startDateTime(request.getStartDateTime())
                .endDateTime(request.getEndDateTime())
                .limitOfEnrollments(request.getLimitOfEnrollments())
                .build();

        event.editEvent(eventEditor);

        return EventUpdateResponse.of(event);
    }

    public void deleteEvent(Long userId, Long eventId, String path) {
        // 해당 이벤트가 존재하고 권한이 있는지 체크용
        eventRepository.findEventByIdIfAuthorized(userId, eventId, path).orElseThrow(EventNotFoundException::new);
        eventRepository.deleteById(eventId);
    }

    public EnrollmentCreateResponse createEnrollment(Long userId, Long eventId) {
        User user = userService.getById(userId);
        Event event = eventRepository.findById(eventId).orElseThrow(EventNotFoundException::new);

        if (enrollmentRepository.existsByEventAndUser(event, user)) {
            throw new EnrollmentAlreadyExistException();
        }
        Enrollment enrollment = Enrollment.builder()
                .enrolledAt(LocalDateTime.now())
                .accepted(event.isAbleToAccept())
                .user(user)
                .build();

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        return EnrollmentCreateResponse.of(savedEnrollment);
    }

    public void cancelEnrollment(Long userId, Long eventId) {
        User user = userService.getById(userId);
        Event event = eventRepository.findById(eventId).orElseThrow(EventNotFoundException::new);

        Enrollment enrollment = enrollmentRepository.findByEventAndUser(event, user).orElseThrow(InvalidEnrollmentException::new);

        if (!enrollment.isAttended()) {
            event.removeEnrollment(enrollment);
            enrollmentRepository.delete(enrollment);
        }
    }
}

