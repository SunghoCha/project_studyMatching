package com.app;

import com.app.domain.study.Study;
import com.app.domain.study.dto.StudyCreateRequest;
import com.app.domain.study.dto.StudyCreateResponse;
import com.app.domain.study.repository.StudyRepository;
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
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Profile("local")
@Component
@RequiredArgsConstructor
public class InitDb implements ApplicationRunner {

    private final UserService userService;
    private final StudyService studyService;
    private final TagService tagService;
    private final ZoneService zoneService;
    private final StudyZoneService studyZoneService;
    private final StudyTagService studyTagService;
    private final StudyRepository studyRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        log.info("초기 데이터 세팅 시작");
        try {
            tagService.initTagData();
            zoneService.initZoneData();
            User user = initUser();
            initStudies(user.getId());
            log.info("초기 데이터 세팅 완료");
        } catch (Exception e) {
            log.error("초기화 작업 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }

    private User initUser() {
        User user = User.builder()
                .name("관리자")
                .email("admin@example.com")
                .role(Role.ADMIN)
                .build();

        return userService.save(user);
    }

    private void initStudies(Long userId) {
        log.info("스터디 초기화 시작");

        // 태그와 Zone 데이터
        List<Tag> tags = tagService.findAll(); // 저장된 태그 목록 가져오기
        List<Zone> zones = zoneService.findAll(); // 저장된 Zone 목록 가져오기

        // 주제 템플릿
        List<String> studyTopics = List.of(
                "웹 개발 입문",
                "프론트엔드 마스터 클래스",
                "백엔드 전문가 과정",
                "자바스크립트 심화 스터디",
                "자바와 스프링 핵심 스터디",
                "풀스택 개발 워크샵",
                "HTML & CSS 디자인 원칙",
                "Node.js와 Express로 API 개발",
                "React와 Redux로 상태 관리",
                "Vue.js를 활용한 인터랙티브 UI 제작"
        );

        // 설명 템플릿
        List<String> shortDescriptions = List.of(
                "초보자를 위한 쉽고 재미있는 강의입니다.",
                "현업 개발자가 직접 설계한 커리큘럼입니다.",
                "스터디 구성원들과 협력하여 프로젝트를 완성합니다.",
                "코딩의 기본부터 심화까지 단계적으로 배울 수 있습니다.",
                "주요 기술 스택을 중심으로 한 집중 학습 과정입니다."
        );

        List<String> fullDescriptions = List.of(
                "이 스터디는 주제에 대한 전반적인 이해를 돕고 실제 프로젝트를 통해 경험을 쌓도록 설계되었습니다.",
                "각 세션마다 실습과 코드 리뷰를 진행하며, 구성원 간의 상호 피드백을 중시합니다.",
                "최신 기술 트렌드와 관련된 다양한 자료를 학습하며, 이를 실무에 적용하는 방법을 배우게 됩니다.",
                "팀 프로젝트를 통해 협업의 중요성을 느끼고 실제 개발 과정을 체험할 수 있습니다.",
                "전문 멘토와 함께 문제를 해결하며 실력을 키울 수 있는 실질적인 과정입니다."
        );

        // 랜덤 데이터 생성
        Random random = new Random();

        for (int i = 1; i <= 50; i++) {
            String path = "study-" + i;
            String title = studyTopics.get(random.nextInt(studyTopics.size())) + " " + i;
            String shortDescription = shortDescriptions.get(random.nextInt(shortDescriptions.size()));
            String fullDescription = fullDescriptions.get(random.nextInt(fullDescriptions.size()));

            StudyCreateRequest request = StudyCreateRequest.builder()
                    .path(path)
                    .title(title)
                    .shortDescription(shortDescription)
                    .fullDescription(fullDescription)
                    .build();

            // 스터디 생성
            StudyCreateResponse response = studyService.createStudy(userId, request);
            log.info("스터디 생성: Path = {}, Title = {}", response.getPath(), title);

            Study study = studyRepository.findByPath(path).orElseThrow(StudyNotFoundException::new);
            study.publish();
            study.startRecruit();

            // 생성된 스터디에 Zone 추가
            addZonesToStudy(userId, path, zones);

            // 생성된 스터디에 Tag 추가
            addTagsToStudy(userId, path, title, tags);
        }
        log.info("스터디 초기화 완료");
    }

    private void addZonesToStudy(Long userId, String path, List<Zone> zones) {
        Random random = new Random();

        // 랜덤으로 2~3개의 Zone 선택
        Set<Long> zoneIds = random.ints(0, zones.size())
                .distinct()
                .limit(3)
                .mapToObj(zones::get)
                .map(Zone::getId)
                .collect(Collectors.toSet());

        StudyZoneCreateRequest request = StudyZoneCreateRequest.builder()
                .zoneIds(zoneIds)
                .build();

        // Zone 추가
        System.out.println(" ========================== init ========================");
        studyZoneService.createStudyZone(userId, path, request);
    }

    private void addTagsToStudy(Long userId, String path, String title, List<Tag> tags) {
        Random random = new Random();

        // 제목과 관련된 태그 찾기
        Set<String> relatedTags = tags.stream()
                .map(Tag::getTitle)
                .filter(title::contains) // 제목에 포함된 태그 선택
                .collect(Collectors.toSet());

        // 관련 태그가 부족하면 랜덤 태그 추가
        while (relatedTags.size() < 2) {
            String randomTag = tags.get(random.nextInt(tags.size())).getTitle();
            relatedTags.add(randomTag);
        }

        StudyTagCreateRequest request = StudyTagCreateRequest.builder()
                .tags(relatedTags)
                .build();

        // Tag 추가
        studyTagService.createStudyTags(userId, path, request);
    }
}
