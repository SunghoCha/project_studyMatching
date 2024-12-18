package com.app.domain.event.dto;

import com.app.domain.event.Enrollment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class EnrollmentCreateResponse {

    private Long eventId;
    private Long enrollmentId;
    private LocalDateTime enrolledAt;
    private boolean accepted;
    private boolean attended;

    @Builder
    public EnrollmentCreateResponse(Long eventId, Long enrollmentId, LocalDateTime enrolledAt, boolean accepted, boolean attended) {
        this.eventId = eventId;
        this.enrollmentId = enrollmentId;
        this.enrolledAt = enrolledAt;
        this.accepted = accepted;
        this.attended = attended;
    }

    public static EnrollmentCreateResponse of(Enrollment enrollment) {

        return EnrollmentCreateResponse.builder()
                .eventId(enrollment.getEvent().getId())
                .enrollmentId(enrollment.getId())
                .enrolledAt(enrollment.getEnrolledAt())
                .accepted(enrollment.isAccepted())
                .attended(enrollment.isAttended())
                .build();
    }
}
