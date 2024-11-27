package com.app.domain.event.controller;

import com.app.domain.event.dto.EventCreateRequest;
import com.app.domain.event.dto.EventCreateResponse;
import com.app.domain.event.dto.EventResponse;
import com.app.domain.event.dto.EventsResponse;
import com.app.domain.event.service.EventService;
import com.app.domain.event.util.EventValidator;
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

    private final EventValidator eventValidator;
    private final EventService eventService;

    @PostMapping("/new-event")
    public ResponseEntity<EventCreateResponse> createEvent(@LoginUser CurrentUser currentUser,
                                                           @PathVariable("path") String path,
                                                           @Valid @RequestBody EventCreateRequest request,
                                                           BindingResult bindingResult) {
        eventValidator.validate(request, bindingResult);
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
    public ResponseEntity<>


}
