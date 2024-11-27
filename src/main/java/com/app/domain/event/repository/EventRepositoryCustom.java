package com.app.domain.event.repository;

import com.app.domain.event.Event;

import java.util.List;

public interface EventRepositoryCustom {

    List<Event> findAllByPath(String path);
}
