package com.app.domain.event.service;

import com.app.domain.event.Event;
import com.app.domain.event.dto.EventCreateRequest;
import com.app.domain.event.dto.EventCreateResponse;
import com.app.domain.event.dto.EventResponse;
import com.app.domain.event.dto.EventsResponse;
import com.app.domain.event.repository.EventRepository;
import com.app.domain.study.Study;
import com.app.domain.study.service.StudyService;
import com.app.domain.user.service.UserService;
import com.app.global.error.exception.EventNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PatchMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final StudyService studyService;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

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

}
