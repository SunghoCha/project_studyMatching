package com.app.domain.user.userZone.repository;

import com.app.domain.user.userZone.UserZone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserZoneRepository extends JpaRepository<UserZone, Long> {

    List<UserZone> findAllByUserId(Long userId);

    void deleteAllByUserId(Long userId);
}
