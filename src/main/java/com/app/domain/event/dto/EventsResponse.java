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

    private List<EventSummaryResponse> newEvents;
    private List<EventSummaryResponse> oldEvents;

    @Builder
    public EventsResponse(List<EventSummaryResponse> newEvents, List<EventSummaryResponse> oldEvents) {
        this.newEvents = newEvents;
        this.oldEvents = oldEvents;
    }
}
