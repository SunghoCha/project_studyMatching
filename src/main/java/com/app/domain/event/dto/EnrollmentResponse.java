package com.app.domain.event.dto;

import com.app.domain.event.Enrollment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class EnrollmentResponse {

    private Long enrollmentId;
    private LocalDateTime enrolledAt;
    private boolean accepted;
    private boolean attended;

    @Builder
    public EnrollmentResponse(Long enrollmentId, LocalDateTime enrolledAt, boolean accepted, boolean attended) {
        this.enrollmentId = enrollmentId;
        this.enrolledAt = enrolledAt;
        this.accepted = accepted;
        this.attended = attended;
    }

    public static EnrollmentResponse of(Enrollment enrollment) {

        return EnrollmentResponse.builder()
                .enrollmentId(enrollment.getId())
                .enrolledAt(enrollment.getEnrolledAt())
                .accepted(enrollment.isAccepted())
                .attended(enrollment.isAttended())
                .build();
    }
}
