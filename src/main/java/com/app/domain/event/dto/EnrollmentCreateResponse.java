package com.app.domain.event.dto;

import com.app.domain.event.Enrollment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class EnrollmentCreateResponse {

    private Long enrollmentId;
    private LocalDateTime enrolledAt;
    private boolean accepted;
    private boolean attended;

    @Builder
    public EnrollmentCreateResponse(Long enrollmentId, LocalDateTime enrolledAt, boolean accepted, boolean attended) {
        this.enrollmentId = enrollmentId;
        this.enrolledAt = enrolledAt;
        this.accepted = accepted;
        this.attended = attended;
    }

    public static EnrollmentCreateResponse of(Enrollment enrollment) {

        return EnrollmentCreateResponse.builder()
                .enrollmentId(enrollment.getId())
                .enrolledAt(enrollment.getEnrolledAt())
                .accepted(enrollment.isAccepted())
                .attended(enrollment.isAttended())
                .build();
    }
}
