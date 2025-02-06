package com.app.domain.event.dto;

import com.app.domain.event.Enrollment;
import com.app.domain.event.Event;
import com.app.domain.user.User;
import lombok.Builder;
import lombok.Getter;

import java.time.Clock;
import java.time.LocalDateTime;

@Getter
public class EnrollmentResponse {

    private Long enrollmentId;
    private String userName;
    private String email;
    private LocalDateTime enrolledAt;
    private boolean accepted;
    private boolean attended;

    @Builder
    public EnrollmentResponse(Long enrollmentId, String userName, String email,
                              LocalDateTime enrolledAt, boolean accepted, boolean attended) {
        this.enrollmentId = enrollmentId;
        this.userName = userName;
        this.email = email;
        this.enrolledAt = enrolledAt;
        this.accepted = accepted;
        this.attended = attended;
    }

    public static EnrollmentResponse of(Enrollment enrollment) {

        return EnrollmentResponse.builder()
                .enrollmentId(enrollment.getId())
                .userName(enrollment.getUser() != null ? enrollment.getUser().getName() : null)
                .email(enrollment.getUser() != null ? enrollment.getUser().getEmail() : null)
                .enrolledAt(enrollment.getEnrolledAt())
                .accepted(enrollment.isAccepted())
                .attended(enrollment.isAttended())
                .build();
    }
}
