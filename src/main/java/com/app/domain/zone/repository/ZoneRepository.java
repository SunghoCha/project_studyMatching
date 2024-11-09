package com.app.domain.zone.repository;

import com.app.domain.userZone.dto.UserZoneUpdateRequest;
import com.app.domain.zone.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ZoneRepository extends JpaRepository<Zone, Long> {

    Optional<Zone> findByCityAndLocalNameAndProvince(String city, String localName, String province);
}
