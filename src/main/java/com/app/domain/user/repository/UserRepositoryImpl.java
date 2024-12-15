package com.app.domain.user.repository;

import com.app.domain.tag.QTag;
import com.app.domain.tag.Tag;
import com.app.domain.user.QUser;
import com.app.domain.user.User;
import com.app.domain.user.userTag.QUserTag;
import com.app.domain.user.userZone.QUserZone;
import com.app.domain.zone.QZone;
import com.app.domain.zone.Zone;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public List<User> findUserByTagsAndZones(List<Tag> tags, List<Zone> zones) {
        QUser user = QUser.user;
        QUserTag userTag = QUserTag.userTag;
        QUserZone userZone = QUserZone.userZone;
        QTag tag = QTag.tag;
        QZone zone = QZone.zone;
        return queryFactory
                .select(user)
                .from(user)
                .leftJoin(user.userTags, userTag)
                .leftJoin(userTag.tag, tag)
                .leftJoin(user.userZones, userZone)
                .leftJoin(userZone.zone, zone)
                .where(
                        tagsIn(user, tags),
                        zonesIn(user, zones)
                )
                .fetch();
    }

    private BooleanExpression tagsIn(QUser user, List<Tag> tags) {
        if (tags == null || tags.isEmpty()) {
            return Expressions.asBoolean(false).isTrue();
        }
        return user.userTags.any().tag.in(tags);
    }

    private BooleanExpression zonesIn(QUser user, List<Zone> zones) {
        if (zones == null || zones.isEmpty()) {
            return Expressions.asBoolean(false).isTrue();
        }
        return user.userZones.any().zone.in(zones);
    }
}
