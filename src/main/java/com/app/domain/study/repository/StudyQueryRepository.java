package com.app.domain.study.repository;

import com.app.domain.common.dto.PagedResponse;
import com.app.domain.study.QStudy;
import com.app.domain.study.Study;
import com.app.domain.study.dto.QStudyQueryResponse;
import com.app.domain.study.dto.StudyQueryResponse;
import com.app.domain.study.dto.StudySearchCond;
import com.app.domain.study.studyTag.QStudyTag;
import com.app.domain.study.studyZone.QStudyZone;
import com.app.domain.tag.QTag;
import com.app.domain.tag.dto.QTagResponse;
import com.app.domain.tag.dto.TagResponse;
import com.app.domain.zone.QZone;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.querydsl.core.group.GroupBy.groupBy;

@Slf4j
@Repository
@RequiredArgsConstructor
public class StudyQueryRepository {

    private final JPAQueryFactory queryFactory;

    public PagedResponse<StudyQueryResponse> findAllStudies(Pageable pageable) {
        QStudy study = QStudy.study;

        // 총 데이터 수
        Long totalCount = queryFactory
                .select(study.count())
                .from(study)
                .fetchOne();

        // 현재 페이지 데이터
        log.info("===============스터디 페이징 시작");
        List<Study> studies = queryFactory
                .select(study)
                .from(study)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        log.info("===============스터디 페이징 완료");

        //  지연로딩 초기화 + batch size
        log.info("===============지연로딩 초기화 시작");
        studies.forEach(s -> {
            Hibernate.initialize(s.getStudyTags());
            Hibernate.initialize(s.getStudyZones());
            Hibernate.initialize(s.getStudyMembers());
        });
        log.info("===============지연로딩 초기화 완료");

        // study <-> dto 변환
        List<StudyQueryResponse> studyQueryResponses = studies.stream()
                .map(StudyQueryResponse::of)
                .toList();

        // 총 페이지
        int totalPages = (int) Math.ceil((double) (totalCount != null ? totalCount : 0) / pageable.getPageSize());

        return PagedResponse.<StudyQueryResponse>builder()
                .content(studyQueryResponses)
                .currentPage(pageable.getPageNumber() + 1)
                .totalPages(totalPages)
                .totalCount(totalCount != null ? totalCount : 0)
                .size(pageable.getPageSize())
                .build();
    }

    public List<StudyQueryResponse> searchStudies(StudySearchCond searchCond, Pageable pageable) {
        QStudy study = QStudy.study;
        QStudyTag studyTag = QStudyTag.studyTag;
        QTag tag = QTag.tag;

        List<Long> studyIds = queryFactory
                .select(study.id)
                .distinct()
                .from(study)
                .leftJoin(study.studyTags, studyTag)
                .leftJoin(studyTag.tag, tag)
                .where(
                        tagInCondition(searchCond.getTags())
                        // TODO zone 조건 추가
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        log.info("=========ids: {}", studyIds.size());

        Map<Long, StudyQueryResponse> groupedResult = queryFactory
                .from(study)
                .leftJoin(study.studyTags, studyTag)
                .leftJoin(studyTag.tag, tag)
                .where(study.id.in(studyIds))
                .transform(
                        groupBy(study.id).as(
                                new QStudyQueryResponse(
                                        study.path,
                                        study.title,
                                        study.shortDescription,
                                        GroupBy.list(new QTagResponse(tag.title))
                                )
                        )
                );
        log.info("=================result: {}", groupedResult.values().toString());

        List<Study> studies = queryFactory
                .select(study)
                .from(study)
                .leftJoin(study.studyTags, studyTag)
                .leftJoin(studyTag.tag, tag)
                .where(study.id.in(studyIds))
                .fetch();
        log.info("=========studies: {}", studies.size());

        return new ArrayList<>(groupedResult.values());
    }

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
