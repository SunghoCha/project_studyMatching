package com.app.domain.userTag.repository;

import com.app.domain.userTag.UserTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface UserTagRepository extends JpaRepository<UserTag, Long> {

    List<UserTag> findByUserId(Long userId);

    void deleteAllByUserId(Long userId);
}
