package com.app.domain.userZone.service;

import com.app.domain.user.User;
import com.app.domain.user.repository.UserRepository;
import com.app.domain.userZone.UserZone;
import com.app.domain.userZone.dto.UserZoneResponse;
import com.app.domain.userZone.dto.UserZoneUpdateResponse;
import com.app.domain.userZone.dto.UserZoneUpdateRequest;
import com.app.domain.userZone.repository.UserZoneRepository;
import com.app.domain.zone.Zone;
import com.app.domain.zone.repository.ZoneRepository;
import com.app.domain.zone.service.ZoneService;
import com.app.global.error.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserZoneService {

    private final UserRepository userRepository;
    private final UserZoneRepository userZoneRepository;
    private final ZoneService zoneService;
    private final ZoneRepository zoneRepository;

    public UserZoneResponse getUserZones(Long userId) {
        Set<UserZone> userZones = userZoneRepository.findAllByUserId(userId);

        return UserZoneResponse.of(userZones);
    }

    public UserZoneUpdateResponse updateUserZones(Long userId, UserZoneUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        Set<UserZone> userZones = request.getZoneIds().stream()
                .map(zoneId -> {
                    Zone zone = zoneService.findById(zoneId);
                    return UserZone.builder()
                            .user(user)
                            .zone(zone)
                            .build();
                })
                .collect(Collectors.toSet());

        userZoneRepository.deleteAllByUserId(userId);
        userZoneRepository.saveAll(userZones);
        user.setUserZones(userZones);

        return UserZoneUpdateResponse.of(userZones);
    }
}
