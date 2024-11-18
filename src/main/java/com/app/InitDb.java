package com.app;

import com.app.domain.study.dto.StudyCreateRequest;
import com.app.domain.study.dto.StudyCreateResponse;
import com.app.domain.study.service.StudyService;
import com.app.domain.user.User;
import com.app.domain.user.constant.Role;
import com.app.domain.user.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;


@Slf4j
@Profile("local")
@Component
@RequiredArgsConstructor
public class InitDb {

    private final UserService userService;
    private final StudyService studyService;

    @PostConstruct
    public void init() {
        log.info("initUser 실행");
        User user = initUser();
        initStudies(user.getId());
    }

    private User initUser() {
        User user = User.builder()
                .name("관리자")
                .email("admin@example.com")
                .role(Role.ADMIN)
                .build();

        return userService.save(user);
    }

    private void initStudies(Long id) {
        log.info("스터디 초기화 시작");

        // 태그 목록
        List<String> tags = List.of("JavaScript", "Vue.js", "React", "Node.js", "CSS", "HTML", "Spring", "Java");

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
            StudyCreateResponse response = studyService.createStudy(id, request);
            log.info("스터디 생성 완료: {}", response.getPath());
        }

        log.info("스터디 초기화 완료");
    }


}
