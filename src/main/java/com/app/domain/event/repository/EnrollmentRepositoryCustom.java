package com.app.domain.event.repository;

import com.app.domain.event.Enrollment;
import com.app.domain.event.Event;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepositoryCustom {

    Optional<Enrollment> findEnrollmentWithUserById(Long enrollmentId);

}
