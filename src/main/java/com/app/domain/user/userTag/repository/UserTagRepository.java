package com.app.domain.user.userTag.repository;

import com.app.domain.user.userTag.UserTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserTagRepository extends JpaRepository<UserTag, Long> {

    List<UserTag> findByUserId(Long userId);

    void deleteAllByUserId(Long userId);
}
