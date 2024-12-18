package com.app.domain.event.dto;

import com.app.domain.event.Event;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
public class EventResponse {

    private Long eventId;
    private String title;
    private String description;
    private LocalDateTime endEnrollmentDateTime;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Integer limitOfEnrollments;
    private List<EnrollmentResponse> enrollments;

    @Builder
    public EventResponse(Long eventId, String title, String description, LocalDateTime endEnrollmentDateTime,
                         LocalDateTime startDateTime, LocalDateTime endDateTime, Integer limitOfEnrollments, List<EnrollmentResponse> enrollments) {
        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.endEnrollmentDateTime = endEnrollmentDateTime;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.limitOfEnrollments = limitOfEnrollments;
        this.enrollments = enrollments;
    }

    public static EventResponse of(Event event) {
        // TODO: event-enrollment 쿼리 최적화
        return EventResponse.builder()
                .eventId(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .endEnrollmentDateTime(event.getEndEnrollmentDateTime())
                .startDateTime(event.getStartDateTime())
                .endDateTime(event.getEndDateTime())
                .limitOfEnrollments(event.getLimitOfEnrollments())
                .enrollments(event.getEnrollments() != null ? event.getEnrollments().stream()
                        .map(EnrollmentResponse::of)
                        .toList() : Collections.emptyList())
                .build();
    }
}
