package com.app.domain.study.studyTag.repository;


import com.app.domain.study.QStudy;
import com.app.domain.study.Study;
import com.app.domain.study.studyTag.QStudyTag;
import com.app.domain.study.studyTag.StudyTag;
import com.app.global.error.exception.StudyNotFoundException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class StudyTagRepositoryImpl implements StudyTagRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Long deleteAllInStudy(Study study) {
        QStudyTag studyTag = QStudyTag.studyTag;

        return queryFactory.delete(studyTag)
                .where(studyTag.study.eq(study))
                .execute();
    }

    @Override
    public List<StudyTag> findAllByStudyPath(String path) {
        QStudyTag studyTag = QStudyTag.studyTag;
        QStudy study = QStudy.study;

        boolean isExist = queryFactory.select(study)
                .from(study)
                .where(study.path.eq(path))
                .fetchFirst() != null;
        if (!isExist) {
            throw new StudyNotFoundException();
        }

        return queryFactory.select(studyTag)
                .from(studyTag)
                .join(studyTag.study, study)
                .where(study.path.eq(path))
                .fetch();
    }
}
