package com.app.domain.event.dto;

import com.app.domain.event.Event;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
public class EventCreateResponse {

    private Long eventId;
    private String title;
    private String description;
    private LocalDateTime endEnrollmentDateTime;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Integer limitOfEnrollments;

    @Builder
    public EventCreateResponse(Long eventId, String title, String description, LocalDateTime endEnrollmentDateTime,
                               LocalDateTime startDateTime, LocalDateTime endDateTime, Integer limitOfEnrollments) {
        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.endEnrollmentDateTime = endEnrollmentDateTime;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.limitOfEnrollments = limitOfEnrollments;
    }

    public static EventCreateResponse of(Event event) {
        return EventCreateResponse.builder()
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
