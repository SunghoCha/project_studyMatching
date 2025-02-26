package com.app.domain.study.repository;

import com.app.domain.common.dto.PagedResponse;
import com.app.domain.study.QStudy;
import com.app.domain.study.Study;
import com.app.domain.study.dto.QStudyQueryResponse;
import com.app.domain.study.dto.SearchCond;
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
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.querydsl.core.group.GroupBy.groupBy;

@Slf4j
@Repository
@RequiredArgsConstructor
public class StudyQueryTest2Repository {

    private final JPAQueryFactory queryFactory;

    public Page<Study> findStudiesWithCondition(SearchCond searchCond, Pageable pageable) {
        OrderSpecifier<?>[] orderSpecifiers = getOrderSpecifiers(pageable);
        QStudy study = QStudy.study;

        // 토탈 카운트
        Long totalCount = queryFactory
                .select(study.id.countDistinct())
                .from(study)
                .leftJoin(study.studyTags, QStudyTag.studyTag)
                .leftJoin(QStudyTag.studyTag.tag, QTag.tag)
                .leftJoin(study.studyZones, QStudyZone.studyZone)
                .leftJoin(QStudyZone.studyZone.zone, QZone.zone)
                .where(
                        studyTitlesLike(searchCond.getTitles()),
                        tagTitlesIn(searchCond.getTags()),
                        zoneIdsIn(searchCond.getZoneIds())
                )
                .fetchOne();

        JPAQuery<Study> query = queryFactory
                .select(study)
                .distinct()
                .from(study);

        if (searchCond.getTags() != null && !searchCond.getTags().isEmpty()) {
            query
                    .leftJoin(study.studyTags, QStudyTag.studyTag).fetchJoin()
                    .leftJoin(QStudyTag.studyTag.tag, QTag.tag).fetchJoin();
        }
        if (searchCond.getZoneIds() != null && !searchCond.getZoneIds().isEmpty()) {
            query
                    .leftJoin(study.studyZones, QStudyZone.studyZone).fetchJoin()
                    .leftJoin(QStudyZone.studyZone.zone, QZone.zone).fetchJoin();
        }

        List<Study> studies = query
                .where(
                        studyTitlesLike(searchCond.getTitles()),
                        tagTitlesIn(searchCond.getTags()),
                        zoneIdsIn(searchCond.getZoneIds())
                )
                .orderBy(orderSpecifiers)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        System.out.println(" ===================================== ");
        System.out.println("query = " + query.toString());


        return new PageImpl<>(studies, pageable, (totalCount != null) ? totalCount : 0L);

    }


    public Page<Study> findStudiesWithCondition2(SearchCond searchCond, Pageable pageable) {
        OrderSpecifier<?>[] orderSpecifiers = getOrderSpecifiers(pageable);
        QStudy study = QStudy.study;

        // 토탈 카운트
        Long totalCount = queryFactory
                .select(study.id.countDistinct())
                .from(study)
                .leftJoin(study.studyTags, QStudyTag.studyTag)
                .leftJoin(QStudyTag.studyTag.tag, QTag.tag)
                .leftJoin(study.studyZones, QStudyZone.studyZone)
                .leftJoin(QStudyZone.studyZone.zone, QZone.zone)
                .where(
                        studyTitlesLike(searchCond.getTitles()),
                        tagTitlesIn(searchCond.getTags()),
                        zoneIdsIn(searchCond.getZoneIds())
                )
                .fetchOne();

        // Id만 먼저 조회 (빠른 중복 제거)
        JPAQuery<Long> query = queryFactory
                .select(study.id)
                .distinct()
                .from(study);

        if (searchCond.getTags() != null && !searchCond.getTags().isEmpty()) {
            query
                    .leftJoin(study.studyTags, QStudyTag.studyTag)
                    .leftJoin(QStudyTag.studyTag.tag, QTag.tag);
        }
        if (searchCond.getZoneIds() != null && !searchCond.getZoneIds().isEmpty()) {
            query
                    .leftJoin(study.studyZones, QStudyZone.studyZone)
                    .leftJoin(QStudyZone.studyZone.zone, QZone.zone);
        }

        List<Long> studyIds = query.where(
                        studyTitlesLike(searchCond.getTitles()),
                        tagTitlesIn(searchCond.getTags()),
                        zoneIdsIn(searchCond.getZoneIds())
                )
                .orderBy(orderSpecifiers)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        //  ID 기반으로 스터디 데이터 조회
        JPAQuery<Study> studyQuery = queryFactory
                .select(study)
                .from(study);

        if (searchCond.getTags() != null && !searchCond.getTags().isEmpty()) {
            query
                    .leftJoin(study.studyTags, QStudyTag.studyTag).fetchJoin()
                    .leftJoin(QStudyTag.studyTag.tag, QTag.tag).fetchJoin();
        }
        if (searchCond.getZoneIds() != null && !searchCond.getZoneIds().isEmpty()) {
            query
                    .leftJoin(study.studyZones, QStudyZone.studyZone).fetchJoin()
                    .leftJoin(QStudyZone.studyZone.zone, QZone.zone).fetchJoin();
        }

        List<Study> studies = studyQuery
                .where(study.id.in(studyIds))
                .orderBy(orderSpecifiers)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        System.out.println(" ===================================== ");
        System.out.println("query2 = " + query.toString());


        return new PageImpl<>(studies, pageable, (totalCount != null) ? totalCount : 0L);
    }


    private BooleanExpression studyTitlesLike(String title) {
        if (title == null || title.isEmpty()) {
            return Expressions.asBoolean(true).isTrue();
        }
        return QStudy.study.title.containsIgnoreCase(title);
    }

    private BooleanExpression tagTitlesIn(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return Expressions.asBoolean(true).isTrue();
        }

        return QTag.tag.title.in(tags);
    }

    private BooleanExpression zoneIdsIn(List<Long> zoneIds) {
        if (zoneIds == null || zoneIds.isEmpty()) {
            return Expressions.asBoolean(true).isTrue();
        }

        return QZone.zone.id.in(zoneIds);
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Pageable pageable) {
        QStudy study = QStudy.study;

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

        return orderSpecifiers.toArray(new OrderSpecifier[0]);
    }

}