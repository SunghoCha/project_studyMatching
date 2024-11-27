package com.app.domain.event.repository;

import com.app.domain.event.Event;
import com.app.domain.event.QEvent;
import com.app.domain.study.QStudy;
import com.app.domain.study.Study;
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
}
