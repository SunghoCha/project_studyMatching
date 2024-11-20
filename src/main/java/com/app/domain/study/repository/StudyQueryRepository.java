package com.app.domain.study.repository;

import com.app.domain.study.QStudy;
import com.app.domain.study.Study;
import com.app.domain.study.dto.StudyQueryResponse;
import com.app.domain.study.dto.StudySearchCond;
import com.app.domain.study.studyTag.QStudyTag;
import com.app.domain.study.studyZone.QStudyZone;
import com.app.domain.tag.QTag;
import com.app.domain.tag.dto.TagResponse;
import com.app.domain.zone.QZone;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.querydsl.core.group.GroupBy.groupBy;
import static java.util.Collections.list;

@Repository
@RequiredArgsConstructor
public class StudyQueryRepository {

    private final JPAQueryFactory queryFactory;


    public List<Study> findAllStudies(Pageable pageable) {
        QStudy study = QStudy.study;

        List<Study> studies = queryFactory
                .select(study)
                .from(study)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        //  지연로딩 초기화 + batch size
        studies.forEach(s -> {
            Hibernate.initialize(s.getStudyTags());
            Hibernate.initialize(s.getStudyZones());
            Hibernate.initialize(s.getStudyMembers());
        });

        return studies;
    }
//    public List<StudyQueryResponse> searchStudies(StudySearchCond searchCond, Pageable pageable) {
//        QStudy study = QStudy.study;
//        QStudyTag studyTag = QStudyTag.studyTag;
//        QTag tag = QTag.tag;
//
//        // Step 1: 일대다 데이터에서 조건절로 추린 후, id 값에 distinct() 적용하여 중복 제거 및 페이징 처리
//        List<Long> studyIds = queryFactory
//                .select(study.id)
//                .distinct()
//                .from(study)
//                .leftJoin(study.studyTags, studyTag)
//                .leftJoin(studyTag.tag, tag)
//                .where(
//                        tagInCondition(searchCond.getTags())
//                        // zone 조건 추가 가능
//                )
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetch();
//
//        // Step 2: transform과 groupBy를 사용해 DTO로 변환
//        Map<Long, StudyQueryResponse> groupedResult = queryFactory
//                .from(study)
//                .leftJoin(study.studyTags, studyTag)
//                .leftJoin(studyTag.tag, tag)
//                .where(study.id.in(studyIds))
//                .transform(
//                        groupBy(study.id).as(
//                                new QStudyQueryResponse(
//                                        study.path,
//                                        study.title,
//                                        study.shortDescription,
//                                        list(new QTagResponse(TagResponse.class, tag.title))
//                                )
//                        )
//                );
//
//        // Step 3: Map에서 DTO 리스트 반환
//        return new ArrayList<>(groupedResult.values());
//    }
    public List<StudyQueryResponse> searchStudies2(StudySearchCond searchCond, Pageable pageable) {
        QStudy study = QStudy.study;
        QStudyTag studyTag = QStudyTag.studyTag;
        QStudyZone studyZone = QStudyZone.studyZone;
        QTag tag = QTag.tag;

        //
        // 일대다로 뻥튀기된 데이터에서 조건절로 추린 후 id값에 distinct()걸어서 중복 제거
        // 이 상태에서 페이징 처리
        List<Long> studyIds = queryFactory
                .select(study.id)
                .distinct()
                .from(study)
                .leftJoin(study.studyTags, studyTag)
                .leftJoin(studyTag.tag, tag)
                .where(
                        tagInCondition(searchCond.getTags()),
                        zoneInCondition(searchCond.getZones())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 중복없이 페이징처리된 id값들을 In쿼리로 원하는 데이터 가져옴 (group by 적용)
        List<StudyQueryResponse> responses = queryFactory
                .select(Projections.constructor(
                        StudyQueryResponse.class,
                        study.path,
                        study.title,
                        study.shortDescription,
                        Projections.list(Projections.constructor(
                                TagResponse.class,
                                tag.title
                        ))
                ))
                .from(study)
                .leftJoin(study.studyTags, studyTag)
                .leftJoin(studyTag.tag, tag)
                .where(study.id.in(studyIds))
                .groupBy(study.id)
                .fetch();

        return responses;
    }

    public List<StudyQueryResponse> searchStudies3(String tagTitle) {
        QStudy study = QStudy.study;
        QStudyTag studyTag = QStudyTag.studyTag;
        QStudyZone studyZone = QStudyZone.studyZone;
        QTag tag = QTag.tag;
        return queryFactory
                .select(Projections.constructor(
                        StudyQueryResponse.class,
                        study.path,
                        study.title,
                        study.shortDescription,
                        Projections.list(Projections.constructor(
                                TagResponse.class,
                                tag.title
                        ))
                ))
                .from(study)
                .leftJoin(study.studyTags, studyTag)
                .leftJoin(studyTag.tag, tag)
                .where(tag.title.eq(tagTitle))
                .groupBy(study.id)
                .fetch();
    }


    private BooleanExpression tagInCondition(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return null;
        }
        QTag tag = QTag.tag;
        BooleanExpression in = tag.title.in(tags);
        System.out.println("Generated BooleanExpression: " + in);
        return in;
    }

    private BooleanExpression zoneInCondition(List<String> zones) {
        if (zones == null || zones.isEmpty()) {
            return null;
        }
        QZone zone = QZone.zone;
        return zone.localName.in(zones);
    }


}
