package com.app.domain.event.repository;

import com.app.domain.event.Event;
import com.querydsl.core.Tuple;

import java.util.List;
import java.util.Optional;

public interface EventRepositoryCustom {

    List<Event> findAllByPath(String path);
    List<Event> findAllEventWithEnrollmentByPath(String path);
    Optional<Event> findEventByIdIfAuthorized(Long userId, Long eventId, String path);
    Optional<Event> findEventWithEnrollmentById(Long eventId);
}
