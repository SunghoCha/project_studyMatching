package com.app.domain.userZone.service;

import com.app.WithAccount;
import com.app.domain.user.User;
import com.app.domain.user.repository.UserRepository;
import com.app.domain.userZone.UserZone;
import com.app.domain.userZone.dto.UserZoneResponse;
import com.app.domain.userZone.dto.UserZoneUpdateRequest;
import com.app.domain.userZone.repository.UserZoneRepository;
import com.app.domain.zone.Zone;
import com.app.domain.zone.repository.ZoneRepository;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class UserZoneServiceTest {

    @Autowired
    UserZoneService userZoneService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserZoneRepository userZoneRepository;

    @Autowired
    ZoneRepository zoneRepository;

    @BeforeEach
    void init() {
        zoneRepository.deleteAll();
    }

    @Test
    @WithAccount
    @DisplayName("")
    void update_with_correct_input() {
        // given
        // 유저 정보 가져옴
        OAuth2User principal = (OAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = (String) principal.getAttributes().get("email");
        User user = userRepository.findByEmail(email).orElseThrow(IllegalArgumentException::new);

        // zone 등록
        Set<Zone> zones = createZones("Andong", "안동시", "North Gyeongsang", 5);
        Set<Zone> newZones = createZones("Seoul", "서울특별시", "none", 3);
        List<Zone> findZones = zoneRepository.saveAll(zones);
        List<Zone> findNewZones = zoneRepository.saveAll(newZones);

        // userZone 등록

        Set<UserZone> userZones = createUserZones(user, new HashSet<>(findZones));
        userZoneRepository.saveAll(userZones);

        // when
        // UserZone 업데이트
        Set<UserZoneUpdateRequest> requests = findNewZones
                .stream()
                .map(zone -> UserZoneUpdateRequest.builder()
                        .id(zone.getId())
                        .city(zone.getCity())
                        .localName(zone.getLocalName())
                        .province(zone.getProvince())
                        .build())
                .collect(Collectors.toSet());

        Set<UserZoneResponse> responses = userZoneService.updateUserZones(user.getId(), requests);

        // then
        assertThat(responses).hasSize(3)
                .extracting("city", "localName", "province")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("Seoul0", "서울특별시0", "none0"),
                        Tuple.tuple("Seoul1", "서울특별시1", "none1"),
                        Tuple.tuple("Seoul2", "서울특별시2", "none2")
                );
    }

    private Set<UserZone> createUserZones(User user, Set<Zone> zones) {
        return zones.stream()
                .map(zone -> UserZone.builder()
                        .user(user)
                        .zone(zone)
                        .build())
                .collect(Collectors.toSet());
    }

    private Set<Zone> createZones(String city, String localName, String province, int size) {
        Set<Zone> zones = new HashSet<>();
        for (int i = 0; i < size; i++) {
            Zone zone = Zone.builder()
                    .city(city + i)
                    .localName(localName + i)
                    .province(province + i)
                    .build();
            zones.add(zone);
        }
        return zones;
    }

}