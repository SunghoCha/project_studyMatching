package com.app.domain.event.repository;

import com.app.domain.event.Enrollment;
import com.app.domain.event.Event;
import com.app.domain.event.QEnrollment;
import com.app.domain.event.QEvent;
import com.app.domain.study.QStudy;
import com.app.domain.study.studyManager.QStudyManager;
import com.app.domain.user.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EnrollmentRepositoryImpl implements EnrollmentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Enrollment> findEnrollmentWithUserById(Long enrollmentId) {
        QEnrollment enrollment = QEnrollment.enrollment;
        QUser user = QUser.user;

        return Optional.ofNullable(
                queryFactory
                        .select(enrollment)
                        .from(enrollment)
                        .leftJoin(enrollment.user, user).fetchJoin()
                        .where(enrollment.id.eq(enrollmentId))
                        .fetchOne()
        );
    }
}
