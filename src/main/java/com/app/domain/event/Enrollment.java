package com.app.domain.event;

import com.app.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrollment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private LocalDateTime enrolledAt;

    private boolean accepted;

    private boolean attended;

    public boolean isEnrolledByUser(User user) {
        return this.user.equals(user);
    }

    @Builder
    public Enrollment(Event event, User user, LocalDateTime enrolledAt, boolean accepted, boolean attended) {
        this.event = event;
        this.user = user;
        this.enrolledAt = enrolledAt;
        this.accepted = accepted;
        this.attended = attended;
    }
}
