package com.app.domain.study.studyZone.service;

import com.app.TestUtils;
import com.app.WithAccount;
import com.app.domain.study.Study;
import com.app.domain.study.repository.StudyRepository;
import com.app.domain.study.studyManager.StudyManager;
import com.app.domain.study.studyManager.repository.StudyManagerRepository;
import com.app.domain.study.studyZone.dto.StudyZoneCreateRequest;
import com.app.domain.study.studyZone.dto.StudyZoneCreateResponse;
import com.app.domain.user.User;
import com.app.domain.user.repository.UserRepository;
import com.app.domain.zone.Zone;
import com.app.domain.zone.repository.ZoneRepository;
import com.app.global.error.exception.UserNotFoundException;
import com.app.global.error.exception.ZoneNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class StudyZoneServiceTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    StudyRepository studyRepository;

    @Autowired
    ZoneRepository zoneRepository;

    @Autowired
    StudyManagerRepository studyManagerRepository;

    @Autowired
    StudyZoneService studyZoneService;

    @Test
    @WithAccount
    @DisplayName("studyZone 생성 테스트")
    void create_StudyZone_with_correct_input() {
        // given
        // study 세팅
        String path = "test";
        User user = userRepository.findByEmail("test@gmail.com").orElseThrow(UserNotFoundException::new);
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();

        StudyManager manager = StudyManager.createManager(user, study);
        studyManagerRepository.save(manager);
        study.addManager(manager);
        Study savedStudy = studyRepository.save(study);

        // zone 세팅
        List<Zone> zones = TestUtils.createZones(10);
        Set<Long> zoneIds = zoneRepository.saveAll(zones).stream()
                .map(Zone::getId)
                .collect(Collectors.toSet());

        StudyZoneCreateRequest request = StudyZoneCreateRequest.builder()
                .zoneIds(zoneIds)
                .build();

        // when
        StudyZoneCreateResponse response = studyZoneService.createStudyZone(user.getId(), path, request);

        // then
        assertThat(response.getZoneIds()).hasSize(10);
    }

    @Test
    @WithAccount
    @DisplayName("잘못된 Zone id 전달하면 예외 발생")
    void create_StudyZone_with_wrong_input() {
        // given
        // study 세팅
        String path = "test";
        User user = userRepository.findByEmail("test@gmail.com").orElseThrow(UserNotFoundException::new);
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        StudyManager manager = StudyManager.createManager(user, study);
        studyManagerRepository.save(manager);
        study.addManager(manager);
        Study savedStudy = studyRepository.save(study);

        // zone 세팅
        List<Zone> zones = TestUtils.createZones(10);
        Set<Long> zoneIds = zoneRepository.saveAll(zones).stream()
                .map(Zone::getId)
                .collect(Collectors.toSet());

        long randomId = Math.abs(UUID.randomUUID().getMostSignificantBits());
        StudyZoneCreateRequest request = StudyZoneCreateRequest.builder()
                .zoneIds(Set.of(randomId))
                .build();

        // expected
        assertThatThrownBy(() -> studyZoneService.createStudyZone(user.getId(), path, request)).isInstanceOf(ZoneNotFoundException.class);
    }
}