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
import com.app.domain.zone.dto.QZoneResponse;
import com.app.domain.zone.dto.ZoneResponse;
import com.app.global.error.exception.InvalidSortPropertyException;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
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

    public PagedResponse<StudyQueryResponse> findStudiesWithCond(Predicate whereClause, Pageable pageable) {
        QStudy study = QStudy.study;

        BooleanExpression finalCondition = whereClause != null ? (BooleanExpression) whereClause : Expressions.asBoolean(true).isTrue();
        log.info("Final condition: {}", finalCondition != null ? finalCondition.toString() : "No condition applied");

        // 총 데이터 수
        Long totalCount = queryFactory
                .select(study.count())
                .from(study)
                .where(finalCondition)
                .fetchOne();

        //
        log.info("Sort orders: {}", pageable.getSort());
        List<OrderSpecifier<?>> orderSpecifiers = pageable.getSort()
                .stream()
                .map(order ->
                        switch (order.getProperty()) {
                            case "title" -> (OrderSpecifier<?>) (order.isAscending()
                                    ? study.title.asc()
                                    : study.title.desc());
                            case "publishedDateTime" -> (OrderSpecifier<?>) (order.isAscending()
                                    ? study.publishedDateTime.asc()
                                    : study.publishedDateTime.desc()).nullsLast();
                            default -> throw new InvalidSortPropertyException();
                        })
                .toList();


        // 현재 페이지 데이터
        log.info("===============스터디 페이징 시작");
        List<Study> studies = queryFactory
                .select(study)
                .from(study)
                .where(finalCondition)
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        log.info("===============스터디 페이징 완료");

        //  지연로딩 초기화 + batch size
        log.info("===============지연로딩 초기화 시작");
        studies.forEach(s -> {
            s.getStudyTags().forEach(studyTag -> Hibernate.initialize(studyTag.getTag()));
            s.getStudyZones().forEach(studyZone -> Hibernate.initialize(studyZone.getZone()));
        });
        log.info("===============지연로딩 초기화 완료");

        // study <-> dto 변환
        List<StudyQueryResponse> studyQueryResponses = convertToQueryResponses(studies);

        // 총 페이지
        int totalPages = getTotalPages(pageable, totalCount);

        return PagedResponse.<StudyQueryResponse>builder()
                .content(studyQueryResponses)
                .currentPage(pageable.getPageNumber() + 1) // 페이지는 0이 아닌 1페이지부터
                .totalPages(totalPages)
                .totalCount(totalCount != null ? totalCount : 0)
                .size(pageable.getPageSize())
                .build();
    }

    public PagedResponse<StudyQueryResponse> findAllStudies(StudySearchCond searchCond, Pageable pageable) {
        BooleanExpression expression = searchCond != null
                ? titlesLike(QStudy.study, searchCond.getTitles())
                : Expressions.asBoolean(true).isTrue();

        return findStudiesWithCond(expression, pageable);
    }

    public PagedResponse<StudyQueryResponse> findMyManagedStudies(Long userId, Pageable pageable) {
        BooleanExpression expression = QStudy.study.studyManagers.any().user.id.eq(userId);

        return findStudiesWithCond(expression, pageable);
    }

    public PagedResponse<StudyQueryResponse> findMyJoinedStudies(Long userId, Pageable pageable) {
        BooleanExpression expression = QStudy.study.studyMembers.any().user.id.eq(userId);

        return findStudiesWithCond(expression, pageable);
    }

    private static List<StudyQueryResponse> convertToQueryResponses(List<Study> studies) {

        return studies.stream()
                .map(StudyQueryResponse::of)
                .toList();
    }

    private static int getTotalPages(Pageable pageable, Long totalCount) {
        int totalPages = (int) Math.ceil((double) (totalCount != null ? totalCount : 0) / pageable.getPageSize());

        return Math.max(totalPages, 1);
    }

    private BooleanExpression titlesLike(QStudy study, List<String> titles) {
        if (titles == null || titles.isEmpty()) {
            return null;
        }
        BooleanExpression condition = study.title.containsIgnoreCase(titles.get(0));
        for (int i = 1; i < titles.size(); i++) {
            condition = condition.or(study.title.containsIgnoreCase(titles.get(i)));
        }
        return condition;
    }

    public PagedResponse<StudyQueryResponse> findStudyWishList2(List<String> tags, List<Long> zoneIds, Pageable pageable) {
        QStudy study = QStudy.study;
        QTag tag = QTag.tag;
        QZone zone = QZone.zone;
        QStudyTag studyTag = QStudyTag.studyTag;
        QStudyZone studyZone = QStudyZone.studyZone;

        // 정렬 조건
        List<OrderSpecifier<? extends Comparable>> orderSpecifiers = pageable.getSort()
                .stream()
                .map(order ->
                        switch (order.getProperty()) {
                            case "title" -> (OrderSpecifier<?>) (order.isAscending()
                                    ? study.title.asc()
                                    : study.title.desc());
                            case "publishedDateTime" -> (OrderSpecifier<?>) (order.isAscending()
                                    ? study.publishedDateTime.asc()
                                    : study.publishedDateTime.desc());
                            default -> throw new InvalidSortPropertyException();
                        })
                .toList();

        // 전체 데이터 개수
        Long totalCount = queryFactory
                .select(study.id.countDistinct())
                .from(study)
                .join(study.studyTags, studyTag).on(tagsIn(study, tags))
                .join(studyTag.tag, tag)
                .join(study.studyZones, studyZone).on(zoneIdsIn(study, zoneIds))
                .join(studyZone.zone, zone)
                .fetchOne();


        // 페이징 위한 사전 쿼리 + 메인쿼리
        List<Long> studyIds = queryFactory
                .select(study.id).distinct()
                .from(study)
                .join(study.studyTags, studyTag).on(tagsIn(study, tags))
                .join(studyTag.tag, tag)
                .join(study.studyZones, studyZone).on(zoneIdsIn(study, zoneIds))
                .join(studyZone.zone, zone)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<StudyQueryResponse> studyQueryResponses = queryFactory
                .select(Projections.constructor(StudyQueryResponse.class,
                        study.path,
                        study.title,
                        study.shortDescription,
                        Projections.list(Projections.constructor(TagResponse.class, tag.title)),
                        Projections.list(Projections.constructor(ZoneResponse.class,
                                        zone.id,
                                        zone.city,
                                        zone.localName,
                                        zone.province)
                        )
                ))
                .distinct()
                .from(study)
                .join(study.studyTags, studyTag).on(tagsIn(study, tags))
                .join(studyTag.tag, tag)
                .join(study.studyZones, studyZone).on(zoneIdsIn(study, zoneIds))
                .join(studyZone.zone, zone)
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
                .where(study.id.in(studyIds))
                .fetch();

        int totalPages = getTotalPages(pageable, totalCount);

        return PagedResponse.<StudyQueryResponse>builder()
                .content(studyQueryResponses)
                .currentPage(pageable.getPageNumber() + 1)
                .totalPages(totalPages)
                .totalCount(totalCount != null ? totalCount : 0)
                .size(pageable.getPageSize())
                .build();
    }

    private BooleanExpression tagsIn(QStudy study, List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return Expressions.asBoolean(true).isTrue();
        }
        return study.studyTags.any().tag.title.in(tags);
    }

    private BooleanExpression zoneIdsIn(QStudy study, List<Long> zoneIds) {
        if (zoneIds == null || zoneIds.isEmpty()) {
            return Expressions.asBoolean(true).isTrue();
        }
        return study.studyZones.any().zone.id.in(zoneIds);
    }

    public PagedResponse<StudyQueryResponse> findStudyWishList(List<String> tags, List<Long> zoneIds, Pageable pageable) {
        QStudy study = QStudy.study;
        QTag tag = QTag.tag;
        QZone zone = QZone.zone;
        QStudyTag studyTag = QStudyTag.studyTag;
        QStudyZone studyZone = QStudyZone.studyZone;

        // 정렬 조건
        List<OrderSpecifier<? extends Comparable>> orderSpecifiers = pageable.getSort()
                .stream()
                .map(order ->
                        switch (order.getProperty()) {
                            case "title" -> (OrderSpecifier<?>) (order.isAscending()
                                    ? study.title.asc()
                                    : study.title.desc());
                            case "publishedDateTime" -> (OrderSpecifier<?>) (order.isAscending()
                                    ? study.publishedDateTime.asc()
                                    : study.publishedDateTime.desc());
                            default -> throw new InvalidSortPropertyException();
                        })
                .toList();

        // 전체 데이터 개수
        Long totalCount = queryFactory
                .select(study.id.countDistinct())
                .from(study)
                .join(study.studyTags, studyTag).on(tagsIn(study, tags))
                .join(studyTag.tag, tag)
                .join(study.studyZones, studyZone).on(zoneIdsIn(study, zoneIds))
                .join(studyZone.zone, zone)
                .fetchOne();


        // 페이징 위한 사전 쿼리 + 메인쿼리
        List<Long> studyIds = queryFactory
                .select(study.id).distinct()
                .from(study)
                .join(study.studyTags, studyTag).on(tagsIn(study, tags))
                .join(studyTag.tag, tag)
                .join(study.studyZones, studyZone).on(zoneIdsIn(study, zoneIds))
                .join(studyZone.zone, zone)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Map<Long, StudyQueryResponse> groupedResult = queryFactory
                .from(study)
                .leftJoin(study.studyTags, studyTag)
                .leftJoin(studyTag.tag, tag)
                .leftJoin(study.studyZones, studyZone)
                .leftJoin(studyZone.zone, zone)
                .where(study.id.in(studyIds))
                .distinct()
                .transform(
                        groupBy(study.id).as(
                                new QStudyQueryResponse(
                                        study.path,
                                        study.title,
                                        study.shortDescription,
                                        GroupBy.set(new QTagResponse(tag.title)),
                                        GroupBy.set(new QZoneResponse(zone.id, zone.city, zone.localName, zone.province))
                                )
                        )
                );

        int totalPages = getTotalPages(pageable, totalCount);

        List<StudyQueryResponse> studyQueryResponses = new ArrayList<>(groupedResult.values());

        return PagedResponse.<StudyQueryResponse>builder()
                .content(studyQueryResponses)
                .currentPage(pageable.getPageNumber() + 1)
                .totalPages(totalPages)
                .totalCount(totalCount != null ? totalCount : 0)
                .size(pageable.getPageSize())
                .build();
    }

//    public List<StudyQueryResponse> searchStudies(StudySearchCond searchCond, Pageable pageable) {
//        QStudy study = QStudy.study;
//        QStudyTag studyTag = QStudyTag.studyTag;
//        QTag tag = QTag.tag;
//
//        List<Long> studyIds = queryFactory
//                .select(study.id)
//                .distinct()
//                .from(study)
//                .leftJoin(study.studyTags, studyTag)
//                .leftJoin(studyTag.tag, tag)
//                .where(
//                        tagInCondition(searchCond.getTags())
//                        // TODO zone 조건 추가
//                )
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetch();
//        log.info("=========ids: {}", studyIds.size());
//
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
//                                        GroupBy.list(new QTagResponse(tag.title)),
//                                        GroupBy.list(new QZoneResponse(zone.id, zone.city, zone.localName, zone.province))
//                                )
//                        )
//                );
//        log.info("=================result: {}", groupedResult.values().toString());
//
//        List<Study> studies = queryFactory
//                .select(study)
//                .from(study)
//                .leftJoin(study.studyTags, studyTag)
//                .leftJoin(studyTag.tag, tag)
//                .where(study.id.in(studyIds))
//                .fetch();
//        log.info("=========studies: {}", studies.size());
//
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
                        tagInCondition(searchCond.getTags())
                        //zoneInCondition(searchCond.getZoneIds())
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
