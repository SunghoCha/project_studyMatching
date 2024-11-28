package com.app.domain.event.repository;

import com.app.domain.event.Enrollment;
import com.app.domain.event.Event;
import com.app.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByEventAndUser(Event event, User user);

    Optional<Enrollment> findByEventAndUser(Event event, User user);
}
