package com.app.domain.userZone.service;

import com.app.domain.user.User;
import com.app.domain.user.repository.UserRepository;
import com.app.domain.userZone.UserZone;
import com.app.domain.userZone.dto.UserZoneResponse;
import com.app.domain.userZone.dto.UserZoneUpdateRequest;
import com.app.domain.userZone.repository.UserZoneRepository;
import com.app.domain.zone.Zone;
import com.app.domain.zone.repository.ZoneRepository;
import com.app.domain.zone.service.ZoneService;
import com.app.global.error.ErrorCode;
import com.app.global.error.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
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

    public Set<UserZoneResponse> getUserZones(Long userId) {
        return userZoneRepository.findAllByUserId(userId).stream()
                .map(UserZoneResponse::of)
                .collect(Collectors.toSet());
    }

    public Set<UserZoneResponse> updateUserZones(Long userId, Set<UserZoneUpdateRequest> request) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        zoneService.validate(request);

        List<UserZone> userZones = request.stream()
                .map(zoneRequest -> {
                    Zone savedZone = zoneRepository.findByCityAndLocalNameAndProvince(
                                    zoneRequest.getCity(),
                                    zoneRequest.getLocalName(),
                                    zoneRequest.getProvince())
                            .orElseGet(() -> zoneRepository.save(Zone.builder()
                                    .city(zoneRequest.getCity())
                                    .localName(zoneRequest.getLocalName())
                                    .province(zoneRequest.getProvince())
                                    .build())
                            );

                    return UserZone.builder()
                            .user(user)
                            .zone(savedZone)
                            .build();
                })
                .collect(Collectors.toList());

        userZoneRepository.deleteAllByUserId(userId);
        user.setUserZones(new HashSet<>(userZones));
        userZoneRepository.saveAll(userZones);
        return userZones.stream()
                .map(UserZoneResponse::of)
                .collect(Collectors.toSet());
    }
}
