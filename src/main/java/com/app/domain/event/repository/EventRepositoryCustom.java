package com.app.domain.event.repository;

import com.app.domain.event.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepositoryCustom {

    List<Event> findAllByPath(String path);
    List<Event> findAllEventWithEnrollmentAndUserByPath(String path);
    Optional<Event> findEventWithEnrollmentByIdIfManager(Long userId, Long eventId, String path);
    Optional<Event> findEventWithEnrollmentByIdIfMember(Long userId, Long eventId, String path);
    Optional<Event> findEventWithEnrollmentById(Long eventId);
}
