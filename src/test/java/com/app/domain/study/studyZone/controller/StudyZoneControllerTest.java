package com.app.domain.study.studyZone.controller;

import com.app.TestUtils;
import com.app.WithAccount;
import com.app.domain.study.Study;
import com.app.domain.study.repository.StudyRepository;
import com.app.domain.study.studyManager.StudyManager;
import com.app.domain.study.studyManager.repository.StudyManagerRepository;
import com.app.domain.study.studyZone.StudyZone;
import com.app.domain.study.studyZone.dto.StudyZoneCreateRequest;
import com.app.domain.study.studyZone.dto.StudyZoneDeleteRequest;
import com.app.domain.study.studyZone.dto.StudyZoneUpdateRequest;
import com.app.domain.study.studyZone.repository.StudyZoneRepository;
import com.app.domain.user.User;
import com.app.domain.user.repository.UserRepository;
import com.app.domain.zone.Zone;
import com.app.domain.zone.repository.ZoneRepository;
import com.app.global.error.ErrorCode;
import com.app.global.error.exception.UserNotFoundException;
import com.app.global.error.exception.ZoneNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class StudyZoneControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    StudyRepository studyRepository;

    @Autowired
    ZoneRepository zoneRepository;

    @Autowired
    StudyManagerRepository studyManagerRepository;

    @Autowired
    StudyZoneRepository studyZoneRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @WithAccount
    @DisplayName("studyZone 생성 성공 테스트")
    void create_studyZone_with_correct_input() throws Exception {
        // given
        String path = "test";
        User user = userRepository.findByEmail("test@gmail.com").orElseThrow(UserNotFoundException::new);
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        Study savedStudy = studyRepository.save(study);

        StudyManager manager = StudyManager.createManager(user, savedStudy);
        StudyManager studyManager = studyManagerRepository.save(manager);
        savedStudy.addManager(studyManager);

        List<Zone> zones = TestUtils.createZones(3);
        Set<Long> zoneIds = zoneRepository.saveAll(zones).stream()
                .map(Zone::getId)
                .collect(Collectors.toSet());

        StudyZoneCreateRequest request = StudyZoneCreateRequest.builder()
                .zoneIds(zoneIds)
                .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/study-zone/{path}", path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.zoneIds").value(Matchers.containsInAnyOrder(zoneIds.stream().map(Long::intValue).toArray())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("잘못된 zoneId 요청으로 보내면 예외 발생")
    void create_studyZone_with_wrong_input() throws Exception {
        // given
        String path = "test";
        User user = userRepository.findByEmail("test@gmail.com").orElseThrow(UserNotFoundException::new);
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        Study savedStudy = studyRepository.save(study);

        StudyManager manager = StudyManager.createManager(user, savedStudy);
        StudyManager studyManager = studyManagerRepository.save(manager);
        savedStudy.addManager(studyManager);

        List<Zone> zones = TestUtils.createZones(3);
        Set<Long> zoneIds = zoneRepository.saveAll(zones).stream()
                .map(Zone::getId)
                .collect(Collectors.toSet());

        StudyZoneCreateRequest request = StudyZoneCreateRequest.builder()
                .zoneIds(Set.of(Math.abs(UUID.randomUUID().getMostSignificantBits())))
                .build();

        String json = objectMapper.writeValueAsString(request);
        List<Long> zoneIdlist = zoneIds.stream().toList();

        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/study-zone/{path}", path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value(ErrorCode.ZONE_NOT_EXISTS.getErrorCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage")
                        .value(ErrorCode.ZONE_NOT_EXISTS.getMessage()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("studyZone 조회 성공 테스트")
    void get_studyZone_with_correct_input() throws Exception {
        // given
        String path = "test";
        User user = userRepository.findByEmail("test@gmail.com").orElseThrow(UserNotFoundException::new);
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        Study savedStudy = studyRepository.save(study);

        StudyManager manager = StudyManager.createManager(user, savedStudy);
        StudyManager studyManager = studyManagerRepository.save(manager);
        savedStudy.addManager(studyManager);

        List<Zone> zones = TestUtils.createZones(3);
        Set<Long> zoneIds = zoneRepository.saveAll(zones).stream()
                .map(Zone::getId)
                .collect(Collectors.toSet());

        Set<StudyZone> studyZones = zoneIds.stream()
                .map(zoneId -> {
                    Zone zone = zoneRepository.findById(zoneId).orElseThrow(ZoneNotFoundException::new);
                    return StudyZone.builder()
                            .study(savedStudy)
                            .zone(zone)
                            .build();
                })
                .collect(Collectors.toSet());
        studyZoneRepository.saveAll(studyZones);
        savedStudy.addStudyZones(studyZones);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.get("/study-zone/{path}", path))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.zoneIds")
                        .value(Matchers.containsInAnyOrder(zoneIds.stream().map(Long::intValue).toArray())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("잘못된 path 요청시 예외 발생")
    void get_studyZone_with_wrong_input() throws Exception {
        // given
        String path = "test";
        User user = userRepository.findByEmail("test@gmail.com").orElseThrow(UserNotFoundException::new);
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        Study savedStudy = studyRepository.save(study);

        StudyManager manager = StudyManager.createManager(user, savedStudy);
        StudyManager studyManager = studyManagerRepository.save(manager);
        savedStudy.addManager(studyManager);

        List<Zone> zones = TestUtils.createZones(3);
        Set<Long> zoneIds = zoneRepository.saveAll(zones).stream()
                .map(Zone::getId)
                .collect(Collectors.toSet());

        Set<StudyZone> studyZones = zoneIds.stream()
                .map(zoneId -> {
                    Zone zone = zoneRepository.findById(zoneId).orElseThrow(ZoneNotFoundException::new);
                    return StudyZone.builder()
                            .study(savedStudy)
                            .zone(zone)
                            .build();
                })
                .collect(Collectors.toSet());
        studyZoneRepository.saveAll(studyZones);
        savedStudy.addStudyZones(studyZones);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.get("/study-zone/{path}", "wrong"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value(ErrorCode.STUDY_NOT_EXISTS.getErrorCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage")
                        .value(ErrorCode.STUDY_NOT_EXISTS.getMessage()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("studyZone 수정 성공 테스트")
    void update_studyZone_with_correct_input() throws Exception {
        // given
        String path = "test";
        User user = userRepository.findByEmail("test@gmail.com").orElseThrow(UserNotFoundException::new);
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        Study savedStudy = studyRepository.save(study);

        StudyManager manager = StudyManager.createManager(user, savedStudy);
        StudyManager studyManager = studyManagerRepository.save(manager);
        savedStudy.addManager(studyManager);

        List<Zone> zones = TestUtils.createZones(10);
        List<Long> zoneIds = zoneRepository.saveAll(zones).stream()
                .map(Zone::getId)
                .toList();
        int middle = zoneIds.size() / 2;

        HashSet<Long> firstHalfZoneIds = new HashSet<>(zoneIds.subList(0, middle));
        HashSet<Long> secondHalfZoneIds = new HashSet<>(zoneIds.subList(middle, zoneIds.size()));

        Set<StudyZone> studyZones = firstHalfZoneIds.stream()
                .map(zoneId -> {
                    Zone zone = zoneRepository.findById(zoneId).orElseThrow(ZoneNotFoundException::new);
                    return StudyZone.builder()
                            .study(study)
                            .zone(zone)
                            .build();
                })
                .collect(Collectors.toSet());
        studyZoneRepository.saveAll(studyZones);
        study.addStudyZones(studyZones);

        StudyZoneUpdateRequest request = StudyZoneUpdateRequest.builder()
                .zoneIds(secondHalfZoneIds)
                .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.patch("/study-zone/{path}", path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.zoneIds")
                        .value(Matchers.containsInAnyOrder(secondHalfZoneIds.stream().map(Long::intValue).toArray())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("잘못된 path 요청시 예외 발생")
    void update_studyZone_with_wrong_path() throws Exception {
        // given
        String path = "test";
        User user = userRepository.findByEmail("test@gmail.com").orElseThrow(UserNotFoundException::new);
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        Study savedStudy = studyRepository.save(study);

        StudyManager manager = StudyManager.createManager(user, savedStudy);
        StudyManager studyManager = studyManagerRepository.save(manager);
        savedStudy.addManager(studyManager);

        List<Zone> zones = TestUtils.createZones(10);
        List<Long> zoneIds = zoneRepository.saveAll(zones).stream()
                .map(Zone::getId)
                .toList();
        int middle = zoneIds.size() / 2;

        HashSet<Long> firstHalfZoneIds = new HashSet<>(zoneIds.subList(0, middle));
        HashSet<Long> secondHalfZoneIds = new HashSet<>(zoneIds.subList(middle, zoneIds.size()));

        Set<StudyZone> studyZones = firstHalfZoneIds.stream()
                .map(zoneId -> {
                    Zone zone = zoneRepository.findById(zoneId).orElseThrow(ZoneNotFoundException::new);
                    return StudyZone.builder()
                            .study(study)
                            .zone(zone)
                            .build();
                })
                .collect(Collectors.toSet());
        studyZoneRepository.saveAll(studyZones);
        study.addStudyZones(studyZones);

        StudyZoneUpdateRequest request = StudyZoneUpdateRequest.builder()
                .zoneIds(secondHalfZoneIds)
                .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.patch("/study-zone/{path}", "wrong")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value(ErrorCode.STUDY_NOT_EXISTS.getErrorCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage")
                        .value(ErrorCode.STUDY_NOT_EXISTS.getMessage()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("잘못된 zone Id로 요청시 예외 발생")
    void update_studyZone_with_wrong_zoneId() throws Exception {
        // given
        String path = "test";
        User user = userRepository.findByEmail("test@gmail.com").orElseThrow(UserNotFoundException::new);
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        Study savedStudy = studyRepository.save(study);

        StudyManager manager = StudyManager.createManager(user, savedStudy);
        StudyManager studyManager = studyManagerRepository.save(manager);
        savedStudy.addManager(studyManager);

        List<Zone> zones = TestUtils.createZones(10);
        List<Long> zoneIds = zoneRepository.saveAll(zones).stream()
                .map(Zone::getId)
                .toList();
        int middle = zoneIds.size() / 2;

        HashSet<Long> firstHalfZoneIds = new HashSet<>(zoneIds.subList(0, middle));

        Set<StudyZone> studyZones = firstHalfZoneIds.stream()
                .map(zoneId -> {
                    Zone zone = zoneRepository.findById(zoneId).orElseThrow(ZoneNotFoundException::new);
                    return StudyZone.builder()
                            .study(study)
                            .zone(zone)
                            .build();
                })
                .collect(Collectors.toSet());
        studyZoneRepository.saveAll(studyZones);
        study.addStudyZones(studyZones);

        StudyZoneUpdateRequest request = StudyZoneUpdateRequest.builder()
                .zoneIds(Set.of(12345L, 12312L))
                .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.patch("/study-zone/{path}", path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value(ErrorCode.ZONE_NOT_EXISTS.getErrorCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage")
                        .value(ErrorCode.ZONE_NOT_EXISTS.getMessage()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("studyZone 삭제 성공 테스트")
    void delete_studyZone_with_correct_input() throws Exception {
        // given
        String path = "test";
        User user = userRepository.findByEmail("test@gmail.com").orElseThrow(UserNotFoundException::new);
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        Study savedStudy = studyRepository.save(study);

        StudyManager manager = StudyManager.createManager(user, savedStudy);
        StudyManager studyManager = studyManagerRepository.save(manager);
        savedStudy.addManager(studyManager);

        List<Zone> zones = TestUtils.createZones(10);
        List<Long> zoneIds = zoneRepository.saveAll(zones).stream()
                .map(Zone::getId)
                .toList();
        int middle = zoneIds.size() / 2;

        HashSet<Long> zoneIdsSet = new HashSet<>(zoneIds);
        HashSet<Long> firstHalfZoneIds = new HashSet<>(zoneIds.subList(0, middle));

        Set<StudyZone> studyZones = zoneIdsSet.stream()
                .map(zoneId -> {
                    Zone zone = zoneRepository.findById(zoneId).orElseThrow(ZoneNotFoundException::new);
                    return StudyZone.builder()
                            .study(study)
                            .zone(zone)
                            .build();
                })
                .collect(Collectors.toSet());
        studyZoneRepository.saveAll(studyZones);
        study.addStudyZones(studyZones);

        StudyZoneDeleteRequest request = StudyZoneDeleteRequest.builder()
                .zoneIds(firstHalfZoneIds)
                .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.delete("/study-zone/{path}", path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.zoneIds")
                        .value(Matchers.containsInAnyOrder(firstHalfZoneIds.stream().map(Long::intValue).toArray())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("잘못된 path 요청시 예외 발생")
    void delete_studyZone_with_wrong_path() throws Exception {
        // given
        String path = "test";
        User user = userRepository.findByEmail("test@gmail.com").orElseThrow(UserNotFoundException::new);
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        Study savedStudy = studyRepository.save(study);

        StudyManager manager = StudyManager.createManager(user, savedStudy);
        StudyManager studyManager = studyManagerRepository.save(manager);
        savedStudy.addManager(studyManager);

        List<Zone> zones = TestUtils.createZones(10);
        List<Long> zoneIds = zoneRepository.saveAll(zones).stream()
                .map(Zone::getId)
                .toList();
        int middle = zoneIds.size() / 2;

        HashSet<Long> zoneIdsSet = new HashSet<>(zoneIds);
        HashSet<Long> firstHalfZoneIds = new HashSet<>(zoneIds.subList(0, middle));

        Set<StudyZone> studyZones = zoneIdsSet.stream()
                .map(zoneId -> {
                    Zone zone = zoneRepository.findById(zoneId).orElseThrow(ZoneNotFoundException::new);
                    return StudyZone.builder()
                            .study(study)
                            .zone(zone)
                            .build();
                })
                .collect(Collectors.toSet());
        studyZoneRepository.saveAll(studyZones);
        study.addStudyZones(studyZones);

        StudyZoneDeleteRequest request = StudyZoneDeleteRequest.builder()
                .zoneIds(firstHalfZoneIds)
                .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.delete("/study-zone/{path}", "wrong")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value(ErrorCode.STUDY_NOT_EXISTS.getErrorCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage")
                        .value(ErrorCode.STUDY_NOT_EXISTS.getMessage()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("잘못된 zone Id로 요청시 예외 발생")
    void delete_studyZone_with_wrong_zoneId() throws Exception {
        // given
        String path = "test";
        User user = userRepository.findByEmail("test@gmail.com").orElseThrow(UserNotFoundException::new);
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        Study savedStudy = studyRepository.save(study);

        StudyManager manager = StudyManager.createManager(user, savedStudy);
        StudyManager studyManager = studyManagerRepository.save(manager);
        savedStudy.addManager(studyManager);

        List<Zone> zones = TestUtils.createZones(10);
        List<Long> zoneIds = zoneRepository.saveAll(zones).stream()
                .map(Zone::getId)
                .toList();
        int middle = zoneIds.size() / 2;

        HashSet<Long> zoneIdsSet = new HashSet<>(zoneIds);

        Set<StudyZone> studyZones = zoneIdsSet.stream()
                .map(zoneId -> {
                    Zone zone = zoneRepository.findById(zoneId).orElseThrow(ZoneNotFoundException::new);
                    return StudyZone.builder()
                            .study(study)
                            .zone(zone)
                            .build();
                })
                .collect(Collectors.toSet());
        studyZoneRepository.saveAll(studyZones);
        study.addStudyZones(studyZones);

        StudyZoneDeleteRequest request = StudyZoneDeleteRequest.builder()
                .zoneIds(Set.of(1234567L, 67798L))
                .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.delete("/study-zone/{path}", path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value(ErrorCode.ZONE_NOT_EXISTS.getErrorCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage")
                        .value(ErrorCode.ZONE_NOT_EXISTS.getMessage()))
                .andDo(MockMvcResultHandlers.print());
    }
}