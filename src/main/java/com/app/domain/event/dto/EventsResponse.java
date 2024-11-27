package com.app.domain.event.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EventsResponse {

    private List<EventResponse> newEvents;
    private List<EventResponse> oldEvents;

    @Builder
    public EventsResponse(List<EventResponse> newEvents, List<EventResponse> oldEvents) {
        this.newEvents = newEvents;
        this.oldEvents = oldEvents;
    }
}
