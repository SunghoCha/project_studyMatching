package com.app.domain.user.userZone.service;

import com.app.domain.user.User;
import com.app.domain.user.repository.UserRepository;
import com.app.domain.user.userZone.UserZone;
import com.app.domain.user.userZone.dto.UserZoneUpdateResponse;
import com.app.domain.user.userZone.repository.UserZoneRepository;
import com.app.domain.user.userZone.dto.UserZoneResponse;
import com.app.domain.user.userZone.dto.UserZoneUpdateRequest;
import com.app.domain.zone.Zone;
import com.app.domain.zone.repository.ZoneRepository;
import com.app.domain.zone.service.ZoneService;
import com.app.global.error.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserZoneService {

    private final UserRepository userRepository;
    private final UserZoneRepository userZoneRepository;
    private final ZoneService zoneService;
    private final ZoneRepository zoneRepository;

    public UserZoneResponse findUserZones(Long userId) {
        List<UserZone> userZones = userZoneRepository.findAllByUserId(userId);

        return UserZoneResponse.of(userZones);
    }

    public List<UserZone> getUserZones(Long userId) {
        return userZoneRepository.findAllByUserId(userId);
    }

    public UserZoneUpdateResponse updateUserZones(Long userId, UserZoneUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        List<UserZone> userZones = request.getZoneIds().stream()
                .map(zoneId -> {
                    Zone zone = zoneService.findById(zoneId);
                    return UserZone.builder()
                            .user(user)
                            .zone(zone)
                            .build();
                })
                .toList();

        userZoneRepository.deleteAllByUserId(userId);
        userZoneRepository.saveAll(userZones);
        user.setUserZones(new HashSet<>(userZones));

        return UserZoneUpdateResponse.of(userZones);
    }
}
