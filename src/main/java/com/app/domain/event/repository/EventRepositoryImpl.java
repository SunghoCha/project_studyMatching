package com.app.domain.event.repository;

import com.app.domain.event.Event;
import com.app.domain.event.QEnrollment;
import com.app.domain.event.QEvent;
import com.app.domain.study.QStudy;
import com.app.domain.study.studyManager.QStudyManager;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EventRepositoryImpl implements EventRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Event> findAllByPath(String path) {
        QStudy study = QStudy.study;
        QEvent event = QEvent.event;

        return queryFactory
                .select(event)
                .from(event)
                .join(event.study, study)
                .where(study.path.eq(path))
                .fetch();
    }

    @Override
    public List<Event> findAllEventWithEnrollmentByPath(String path) {
        QStudy study = QStudy.study;
        QEvent event = QEvent.event;
        QEnrollment enrollment = QEnrollment.enrollment;

        return queryFactory
                .select(event)
                .from(event)
                .join(event.study, study)
                .leftJoin(event.enrollments, enrollment).fetchJoin()
                .where(study.path.eq(path))
                .fetch();
    }

    @Override
    public Optional<Event> findEventByIdIfAuthorized(Long userId, Long eventId, String path) {
        QEvent event = QEvent.event;
        QStudy study = QStudy.study;
        QStudyManager studyManager = QStudyManager.studyManager;

        Event result = queryFactory
                .select(event)
                .from(event)
                .join(event.study, study)
                .join(study.studyManagers, studyManager)
                .where(
                        study.path.eq(path),
                        event.id.eq(eventId),
                        studyManager.user.id.eq(userId)
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<Event> findEventWithEnrollmentById(Long eventId) {
        QEvent event = QEvent.event;
        QEnrollment enrollment = QEnrollment.enrollment;

        Event result = queryFactory
                .select(event)
                .from(event)
                .leftJoin(event.enrollments, enrollment).fetchJoin()
                .where(
                        event.id.eq(eventId))
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
