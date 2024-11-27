package com.app.domain.event;

import com.app.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrollment_id")
    private Long id;

    @ManyToOne
    private Event event;

    @ManyToOne
    private User user;

    private LocalDateTime enrolledAt;

    private boolean accepted;

    private boolean attended;

    public boolean isEnrolledByUser(User user) {
        return this.user.equals(user);
    }

}
