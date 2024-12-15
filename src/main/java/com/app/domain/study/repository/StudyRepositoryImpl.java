package com.app.domain.study.repository;

import com.app.domain.event.QEvent;
import com.app.domain.study.QStudy;
import com.app.domain.study.Study;
import com.app.domain.study.studyManager.QStudyManager;
import com.app.domain.study.studyTag.QStudyTag;
import com.app.domain.study.studyZone.QStudyZone;
import com.app.domain.study.studyZone.StudyZone;
import com.app.domain.tag.QTag;
import com.app.domain.zone.QZone;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class StudyRepositoryImpl implements StudyRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Study> findStudyWithManagerByPath(String path) {
        QStudy study = QStudy.study;
        QStudyManager studyManager = QStudyManager.studyManager;
        Study result = queryFactory.select(study)
                .from(study)
                .leftJoin(study.studyManagers, studyManager)
                .where(study.path.eq(path))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<Study> findStudyWithAllByPath(String path) {
        QStudy study = QStudy.study;

        Study result = queryFactory
                .select(study)
                .from(study)
                .where(study.path.eq(path))
                .fetchOne();

        if (result != null) {
            result.getStudyTags().forEach(studyTag -> Hibernate.initialize(studyTag.getTag()));
            result.getStudyZones().forEach(studyZone -> Hibernate.initialize(studyZone.getZone()));
            result.getStudyMembers().forEach(studyMember -> Hibernate.initialize(studyMember.getUser()));
            result.getStudyManagers().forEach(studyManager -> Hibernate.initialize(studyManager.getUser()));
        }

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<Study> findStudyWithTagsAndZonesById(Long studyId) {
        QStudy study = QStudy.study;
        QStudyTag studyTag = QStudyTag.studyTag;
        QTag tag = QTag.tag;
        QStudyZone studyZone = QStudyZone.studyZone;
        QZone zone = QZone.zone;

        return Optional.ofNullable(queryFactory
                .select(study)
                .from(study)
                .leftJoin(study.studyTags, studyTag).fetchJoin()
                .leftJoin(studyTag.tag, tag).fetchJoin()
                .leftJoin(study.studyZones, studyZone).fetchJoin()
                .leftJoin(studyZone.zone, zone).fetchJoin()
                .where(study.id.eq(studyId))
                .fetchOne());
    }


}
