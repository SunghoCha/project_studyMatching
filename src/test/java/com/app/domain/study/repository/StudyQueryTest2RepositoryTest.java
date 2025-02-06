package com.app.domain.study.repository;

import com.app.WithAccount;
import com.app.domain.study.Study;
import com.app.domain.study.dto.SearchCond;
import com.app.domain.study.dto.StudyCreateRequest;
import com.app.domain.study.dto.StudyCreateResponse;
import com.app.domain.study.service.StudyService;
import com.app.domain.study.studyTag.dto.StudyTagCreateRequest;
import com.app.domain.study.studyTag.service.StudyTagService;
import com.app.domain.study.studyZone.dto.StudyZoneCreateRequest;
import com.app.domain.study.studyZone.service.StudyZoneService;
import com.app.domain.tag.Tag;
import com.app.domain.tag.service.TagService;
import com.app.domain.user.User;
import com.app.domain.user.constant.Role;
import com.app.domain.user.service.UserService;
import com.app.domain.zone.Zone;
import com.app.domain.zone.service.ZoneService;
import com.app.global.error.exception.StudyNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@Transactional
@RequiredArgsConstructor
public class StudyQueryTest2RepositoryTest {

    @Autowired
    private StudyQueryTest2Repository studyQueryTest2Repository;

    @Autowired
    private UserService userService;

    @Autowired
    private StudyService studyService;

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private TagService tagService;

    @Autowired
    private ZoneService zoneService;

    @Autowired
    private StudyTagService studyTagService;

    @Autowired
    private StudyZoneService studyZoneService;

    private Long userId;
    private List<Tag> tags;
    private List<Zone> zones;

    @BeforeEach
    void setup() throws IOException {
        log.info("테스트 데이터 초기화 시작");

        // 1. 사용자 생성
        User user = initUser();
        userId = user.getId();

        // 2. 태그 및 지역 데이터 초기화
        tagService.initTagData();
        zoneService.initZoneData();

        tags = tagService.findAll();
        zones = zoneService.findAll();

        log.info("저장된 태그: {}", tags.stream().map(Tag::getTitle).collect(Collectors.toList()));
        log.info("저장된 지역: {}", zones.stream().map(Zone::getLocalName).collect(Collectors.toList()));

        // 3. 테스트용 스터디 데이터 생성
        createSampleStudies(10); // 10개의 샘플 데이터 생성

        log.info("테스트 데이터 초기화 완료");
    }

    @AfterEach
    void cleanup() {
        log.info("테스트 종료 후 데이터 삭제");
        studyRepository.deleteAll();
    }

    private User initUser() {
        User user = User.builder()
                .name("관리자")
                .email("admin@example.com")
                .role(Role.ADMIN)
                .build();

        return userService.save(user);
    }

    private void createSampleStudies(int count) {
        Random random = new Random();

        for (int i = 1; i <= count; i++) {
            String path = "test-study-" + i;
            String title = "테스트 스터디 " + i;

            StudyCreateRequest request = StudyCreateRequest.builder()
                    .path(path)
                    .title(title)
                    .shortDescription("테스트 설명")
                    .fullDescription("테스트 상세 설명")
                    .build();

            // 스터디 생성
            StudyCreateResponse response = studyService.createStudy(userId, request);
            Study study = studyRepository.findByPath(path).orElseThrow(StudyNotFoundException::new);

            log.info("생성된 스터디 [{}]: {}", i, title);

            // 태그 및 지역 추가
            addTagsToStudy(userId, path, title, tags);
            addZonesToStudy(userId, path, zones);
        }
    }

    private void addTagsToStudy(Long userId, String path, String title, List<Tag> tags) {
        Set<String> relatedTags = new HashSet<>();
        relatedTags.add(tags.get(0).getTitle());
        relatedTags.add(tags.get(1).getTitle());

        StudyTagCreateRequest request = StudyTagCreateRequest.builder()
                .tags(relatedTags)
                .build();

        studyTagService.createStudyTags(userId, path, request);
        log.info("스터디 [{}] 태그 추가: {}", path, relatedTags);
    }

    private void addZonesToStudy(Long userId, String path, List<Zone> zones) {
        Set<Long> zoneIds = new HashSet<>();
        zoneIds.add(zones.get(0).getId());
        zoneIds.add(zones.get(1).getId());

        StudyZoneCreateRequest request = StudyZoneCreateRequest.builder()
                .zoneIds(zoneIds)
                .build();

        studyZoneService.createStudyZone(userId, path, request);
        log.info("스터디 [{}] 지역 추가: {}", path, zoneIds);
    }

    @Test
    @WithAccount
    void testFindStudiesWithCondition() {
        log.info("[테스트] findStudiesWithCondition 실행");
        Pageable pageable = PageRequest.of(0, 5);
        SearchCond searchCond = new SearchCond();
        searchCond.setTitles("테스트 스터디");

        Page<Study> result = studyQueryTest2Repository.findStudiesWithCondition(searchCond, pageable);

        log.info("검색된 스터디 개수: {}", result.getTotalElements());
        log.info("검색된 스터디 목록: {}", result.getContent().stream()
                .map(Study::getTitle)
                .collect(Collectors.toList()));

        assertNotNull(result);
        assertEquals(5, result.getContent().size()); // 5개씩 페이징
    }

    @Test
    @WithAccount
    void testFindStudiesWithCondition2() {
        log.info("[테스트] findStudiesWithCondition2 실행");
        Pageable pageable = PageRequest.of(0, 5);
        SearchCond searchCond = new SearchCond();
        searchCond.setTags(List.of("Vue.js"));

        Page<Study> result = studyQueryTest2Repository.findStudiesWithCondition2(searchCond, pageable);

        log.info("검색된 스터디 개수: {}", result.getTotalElements());
        log.info("검색된 스터디 목록: {}", result.getContent().stream()
                .map(Study::getTitle)
                .collect(Collectors.toList()));

        assertNotNull(result);
        assertFalse(result.getContent().isEmpty());
        Assertions.assertThat(result.getContent().size()).isEqualTo(5);
    }
}
