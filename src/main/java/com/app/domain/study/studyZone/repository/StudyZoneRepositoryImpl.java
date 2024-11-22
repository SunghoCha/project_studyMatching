package com.app.domain.study.studyZone.repository;

import com.app.domain.study.studyZone.QStudyZone;
import com.app.domain.study.studyZone.StudyZone;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class StudyZoneRepositoryImpl implements StudyZoneRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<StudyZone> findByStudyAndZoneIds(Long studyId, Set<Long> zoneIds) {
        QStudyZone studyZone = QStudyZone.studyZone;

        return queryFactory
                .select(studyZone)
                .from(studyZone)
                .where(
                        studyZone.study.id.eq(studyId),
                        studyZone.zone.id.in(zoneIds)
                )
                .fetch();
    }

    @Override
    public Long deleteAllByIds(Set<Long> zoneIds) {
        QStudyZone studyZone = QStudyZone.studyZone;

        return queryFactory
                .delete(studyZone)
                .where(studyZone.id.in(zoneIds))
                .execute();
    }
}
