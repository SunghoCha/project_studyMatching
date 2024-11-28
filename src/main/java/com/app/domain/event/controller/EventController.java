package com.app.domain.event.controller;

import com.app.domain.event.dto.*;
import com.app.domain.event.service.EventService;
import com.app.domain.event.util.EventCreateValidator;
import com.app.global.config.auth.LoginUser;
import com.app.global.config.auth.dto.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/study/{path}")
public class EventController {

    private final EventCreateValidator eventCreateValidator;
    private final EventService eventService;

    @PostMapping("/new-event")
    public ResponseEntity<EventCreateResponse> createEvent(@LoginUser CurrentUser currentUser,
                                                           @PathVariable("path") String path,
                                                           @Valid @RequestBody EventCreateRequest request,
                                                           BindingResult bindingResult) {
        eventCreateValidator.validate(request, bindingResult);
        EventCreateResponse response = eventService.createEvent(currentUser.getId(), path, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<EventResponse> getEvent(@PathVariable("path") String path, @PathVariable("id") Long id) {
        EventResponse response = eventService.getEvent(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/events")
    public ResponseEntity<EventsResponse> getEvents(@PathVariable("path") String path) {
        EventsResponse response = eventService.getEvents(path);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/events/{id}")
    public ResponseEntity<EventUpdateResponse> updateEvent(@LoginUser CurrentUser currentUser,
                                                           @PathVariable("path") String path,
                                                           @PathVariable("id") Long eventId,
                                                           @RequestBody EventUpdateRequest request) {
        EventUpdateResponse response = eventService.updateEvent(currentUser.getId(), eventId, path, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<Void> deleteEvent(@LoginUser CurrentUser currentUser,
                                            @PathVariable("path") String path,
                                            @PathVariable("id") Long eventId) {
        eventService.deleteEvent(currentUser.getId(), eventId, path);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/events/{id}/enroll")
    public ResponseEntity<EnrollmentCreateResponse> newEnrollment(@LoginUser CurrentUser currentUser, @PathVariable("path") String path, @PathVariable("id") Long eventId) {
        EnrollmentCreateResponse response = eventService.createEnrollment(currentUser.getId(), eventId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/events/{id}/disenroll")
    public ResponseEntity<Void> cancelEnrollment(@LoginUser CurrentUser currentUser, @PathVariable("path") String path, @PathVariable("id") Long eventId) {
        eventService.cancelEnrollment(currentUser.getId(), eventId);

        return ResponseEntity.noContent().build();
    }


}
