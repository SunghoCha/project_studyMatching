package com.app.domain.user.repository;

import com.app.domain.tag.Tag;
import com.app.domain.user.User;
import com.app.domain.zone.Zone;

import java.util.List;

public interface UserRepositoryCustom {

    List<User> findUserByTagsAndZones(List<Tag> tags, List<Zone> zones);
}
