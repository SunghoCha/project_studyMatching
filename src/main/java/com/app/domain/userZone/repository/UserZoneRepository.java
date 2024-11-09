package com.app.domain.userZone.repository;

import com.app.domain.userZone.UserZone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface UserZoneRepository extends JpaRepository<UserZone, Long> {

    Set<UserZone> findAllByUserId(Long userId);

    void deleteAllByUserId(Long userId);
}
