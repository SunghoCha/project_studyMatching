package com.app.domain.userTag.repository;

import com.app.domain.userTag.UserTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface UserTagRepository extends JpaRepository<UserTag, Long> {

    Set<UserTag> findByUserId(Long userId);
}
