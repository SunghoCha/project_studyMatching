package com.app.domain.study.repository;

import com.app.domain.study.QStudy;
import com.app.domain.study.Study;
import com.app.domain.study.studyManager.QStudyManager;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class StudyRepositoryImpl implements StudyRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Study> findByStudyWithManagerByPath(String path) {
        QStudy study = QStudy.study;
        QStudyManager studyManager = QStudyManager.studyManager;
        Study result = queryFactory.select(study)
                .from(study)
                .leftJoin(study.studyManagers, studyManager)
                .where(study.path.eq(path))
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
