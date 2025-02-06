package com.app.domain.event.dto;

import com.app.domain.event.Event;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
public class EventSummaryResponse {

    private Long eventId;
    private String title;
    private String description;
    private LocalDateTime endEnrollmentDateTime;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Integer limitOfEnrollments;

    @Builder
    public EventSummaryResponse(Long eventId, String title, String description, LocalDateTime endEnrollmentDateTime,
                         LocalDateTime startDateTime, LocalDateTime endDateTime, Integer limitOfEnrollments) {
        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.endEnrollmentDateTime = endEnrollmentDateTime;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.limitOfEnrollments = limitOfEnrollments;
    }

    public static EventSummaryResponse of(Event event) {
        // TODO: event-enrollment 쿼리 최적화
        return EventSummaryResponse.builder()
                .eventId(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .endEnrollmentDateTime(event.getEndEnrollmentDateTime())
                .startDateTime(event.getStartDateTime())
                .endDateTime(event.getEndDateTime())
                .limitOfEnrollments(event.getLimitOfEnrollments())
                .build();
    }
}
