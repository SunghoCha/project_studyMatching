package com.app.domain.event.controller;

import com.app.domain.event.dto.*;
import com.app.domain.event.service.EventService;
import com.app.domain.event.util.EventCreateValidator;
import com.app.domain.event.util.EventUpdateValidator;
import com.app.global.config.auth.LoginUser;
import com.app.global.config.auth.dto.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/study/{path}/events")
public class EventController {

    private final EventCreateValidator eventCreateValidator;
    private final EventUpdateValidator eventUpdateValidator;
    private final EventService eventService;

    @PostMapping("/new")
    public ResponseEntity<EventCreateResponse> createEvent(@LoginUser CurrentUser currentUser,
                                                           @PathVariable("path") String path,
                                                           @Valid @RequestBody EventCreateRequest request,
                                                           BindingResult bindingResult) throws BindException {
        eventCreateValidator.validate(request, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }
        EventCreateResponse response = eventService.createEvent(currentUser.getId(), path, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponse> getEvent(@LoginUser CurrentUser currentUser,
                                                  @PathVariable("path") String path,
                                                  @PathVariable("eventId") Long eventId) {
        EventResponse response = eventService.getEvent(currentUser.getId(), eventId);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<EventsResponse> getEvents(@PathVariable("path") String path) {
        EventsResponse response = eventService.getEvents(path);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventUpdateResponse> updateEvent(@LoginUser CurrentUser currentUser,
                                                           @PathVariable("path") String path,
                                                           @PathVariable("eventId") Long eventId,
                                                           @RequestBody EventUpdateRequest request,
                                                           BindingResult bindingResult) throws BindException {
        eventUpdateValidator.validate(request, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }
        EventUpdateResponse response = eventService.updateEvent(currentUser.getId(), eventId, path, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@LoginUser CurrentUser currentUser,
                                            @PathVariable("path") String path,
                                            @PathVariable("eventId") Long eventId) {
        eventService.deleteEvent(currentUser.getId(), eventId, path);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{eventId}/enroll")
    public ResponseEntity<EnrollmentCreateResponse> newEnrollment(@LoginUser CurrentUser currentUser, @PathVariable("path") String path, @PathVariable("eventId") Long eventId) {
        EnrollmentCreateResponse response = eventService.createEnrollment(currentUser.getId(), eventId, path);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{eventId}/disenroll")
    public ResponseEntity<Void> cancelEnrollment(@LoginUser CurrentUser currentUser,
                                                 @PathVariable("path") String path,
                                                 @PathVariable("eventId") Long eventId) {
        eventService.cancelEnrollment(currentUser.getId(), eventId, path);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{eventId}/enrollments/{enrollmentId}/accept")
    public ResponseEntity<EnrollmentResponse> acceptEnrollment(@LoginUser CurrentUser currentUser,
                                                               @PathVariable("path") String path,
                                                               @PathVariable("eventId") Long eventId,
                                                               @PathVariable("enrollmentId") Long enrollmentId) {
        EnrollmentResponse response = eventService.acceptEnrollment(currentUser.getId(), eventId, enrollmentId, path);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{eventId}/enrollments/{enrollmentId}/reject")
    public ResponseEntity<EnrollmentResponse> rejectEnrollment(@LoginUser CurrentUser currentUser,
                                                               @PathVariable("path") String path,
                                                               @PathVariable("eventId") Long eventId,
                                                               @PathVariable("enrollmentId") Long enrollmentId) {
        EnrollmentResponse response = eventService.rejectEnrollment(currentUser.getId(), eventId, enrollmentId, path);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{eventId}/enrollments/{enrollmentId}/check-in")
    public ResponseEntity<EnrollmentResponse> checkInEnrollment(@LoginUser CurrentUser currentUser,
                                                                @PathVariable("path") String path,
                                                                @PathVariable("eventId") Long eventId,
                                                                @PathVariable("enrollmentId") Long enrollmentId) {
        EnrollmentResponse response = eventService.checkInEnrollment(currentUser.getId(), eventId, enrollmentId, path);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{eventId}/enrollments/{enrollmentId}/cancel-check-in")
    public ResponseEntity<EnrollmentResponse> cancelCheckInEnrollment(@LoginUser CurrentUser currentUser,
                                                                      @PathVariable("eventId") Long eventId,
                                                                      @PathVariable("enrollmentId") Long enrollmentId,
                                                                      @PathVariable("path") String path) {
        EnrollmentResponse response = eventService.cancelCheckInEnrollment(currentUser.getId(), eventId, enrollmentId, path);

        return ResponseEntity.ok(response);
    }
}
