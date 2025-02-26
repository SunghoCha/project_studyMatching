package com.app.domain.study.service;

import com.app.domain.common.dto.PagedResponse;
import com.app.domain.study.QStudy;
import com.app.domain.study.Study;
import com.app.domain.study.StudyEditor;
import com.app.domain.study.dto.*;
import com.app.domain.study.dto.studySetting.StudyPathUpdateRequest;
import com.app.domain.study.dto.studySetting.StudyTitleUpdateRequest;
import com.app.domain.study.eventListener.StudyCreatedEvent;
import com.app.domain.study.eventListener.StudyUpdatedEvent;
import com.app.domain.study.repository.StudyQueryRepository;
import com.app.domain.study.repository.StudyQueryTest2Repository;
import com.app.domain.study.repository.StudyQueryTestRepository;
import com.app.domain.study.repository.StudyRepository;
import com.app.domain.study.studyManager.StudyManager;
import com.app.domain.study.studyManager.repository.StudyManagerRepository;
import com.app.domain.study.studyMember.StudyMember;
import com.app.domain.study.studyMember.repository.StudyMemberRepository;
import com.app.domain.user.User;
import com.app.domain.user.service.UserService;
import com.app.domain.user.userTag.UserTag;
import com.app.domain.user.userTag.service.UserTagService;
import com.app.domain.user.userZone.UserZone;
import com.app.domain.user.userZone.service.UserZoneService;
import com.app.global.error.exception.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StudyTestService {

    private final StudyRepository studyRepository;
    private final UserService userService;
    private final StudyQueryRepository studyQueryRepository;
    private final StudyManagerRepository studyManagerRepository;
    private final StudyMemberRepository studyMemberRepository;
    private final UserTagService userTagService;
    private final UserZoneService userZoneService;
    private final Clock clock;
    private final ApplicationEventPublisher eventPublisher;
    private final StudyQueryTestRepository studyQueryTestRepository;
    private final StudyQueryTest2Repository studyQueryTest2Repository;







    public Study findByPath(String path) {
        return studyRepository.findByPath(path).orElseThrow(StudyNotFoundException::new);
    }

    public Study findAuthorizedStudy(Long userId, String path) {
        User user = userService.getById(userId);
        Study study = findByPath(path);
        if (!study.isManager(user)) {
            throw new UnauthorizedAccessException();
        }
        return study;
    }

    public StudyUpdateResponse updateDescription(Long userId, String path, StudyUpdateRequest request) {
        Study study = findAuthorizedStudy(userId, path);
        StudyEditor studyEditor = study.toEditor()
                .shortDescription(request.getShortDescription())
                .fullDescription(request.getFullDescription())
                .build();
        study.edit(studyEditor);

        eventPublisher.publishEvent(new StudyUpdatedEvent(study, "스터디 소개를 수정했습니다."));

        return StudyUpdateResponse.of(study);
    }

    public Study findStudyWithManager(Long userId, String path) {
        User user = userService.getById(userId);
        Study study = studyRepository.findStudyWithManagerByPath(path).orElseThrow(StudyNotFoundException::new);
        if (!study.isManager(user)) {
            throw new UnauthorizedAccessException();
        }
        return study;
    }

    public StudyResponse joinStudy(Long userId, String path) {
        User user = userService.getById(userId);
        Study study = studyRepository.findStudyWithAllByPath(path).orElseThrow(StudyNotFoundException::new);

        StudyMember member = StudyMember.createMember(user, study);
        StudyMember saveMember = studyMemberRepository.save(member);
        study.addMember(saveMember);

        return StudyResponse.of(user, study);
    }

    public StudyResponse leaveStudy(Long userId, String path) {
        User user = userService.getById(userId);
        Study study = studyRepository.findStudyWithAllByPath(path).orElseThrow(StudyNotFoundException::new);

        StudyMember member = studyMemberRepository.findByStudyAndUser(study, user).orElseThrow(StudyMemberNotFoundException::new);
        study.removeMember(member);
        studyMemberRepository.delete(member);

        return StudyResponse.of(user, study);
    }

    public Boolean publishStudy(Long userId, String path) {
        Study study = findAuthorizedStudy(userId, path);
        study.publish(clock);

        eventPublisher.publishEvent(new StudyCreatedEvent(study));

        return study.isPublished();
    }

    public Boolean closeStudy(Long userId, String path) {
        Study study = findAuthorizedStudy(userId, path);
        study.close(clock);

        eventPublisher.publishEvent(new StudyUpdatedEvent(study, "스터디가 종료되었습니다."));

        return study.isClosed();
    }

    public Boolean startRecruit(Long userId, String path) {
        Study study = findAuthorizedStudy(userId, path);
        study.startRecruit(LocalDateTime.now(clock));

        eventPublisher.publishEvent(new StudyUpdatedEvent(study, "팀원 모집이 시작되었습니다."));

        return study.isRecruiting();
    }

    public Boolean stopRecruit(Long userId, String path) {
        Study study = findAuthorizedStudy(userId, path);
        study.stopRecruit(LocalDateTime.now(clock));

        eventPublisher.publishEvent(new StudyUpdatedEvent(study, "팀원 모집이 중단되었습니다."));

        return study.isRecruiting();
    }

    public String updateStudyPath(Long userId, String path, StudyPathUpdateRequest request) {
        Study study = findAuthorizedStudy(userId, path);
        String newPath = request.getPath();

        if (!study.getPath().equals(newPath) && studyRepository.findByPath(newPath).isPresent()) {
            throw new StudyPathAlreadyExistException();
        }

        StudyEditor studyEditor = study.toEditor()
                .path(newPath)
                .build();
        study.edit(studyEditor);

        return study.getPath();
    }

    public String updateStudyTitle(Long userId, String path, StudyTitleUpdateRequest request) {
        Study study = findAuthorizedStudy(userId, path);
        StudyEditor studyEditor = study.toEditor()
                .title(request.getTitle())
                .build();
        study.edit(studyEditor);

        return study.getTitle();
    }

    public String updateStudyBanner(Long userId, String path, String imageUrl) {
        Study study = findAuthorizedStudy(userId, path);
        StudyEditor studyEditor = study.toEditor()
                .image(imageUrl)
                .build();
        study.edit(studyEditor);

        return study.getImage();
    }

    public PagedResponse<StudyQueryResponse> getStudyWishlist(Long userId, Pageable pageable) {
        List<UserTag> userTags = userTagService.getUserTags(userId);
        List<String> tags = userTags.stream()
                .map(userTag -> userTag.getTag().getTitle())
                .toList();

        List<UserZone> userZones = userZoneService.getUserZones(userId);
        List<Long> zoneIds = userZones.stream()
                .map(userZone -> userZone.getZone().getId())
                .toList();

        return studyQueryRepository.findStudyWishList(tags, zoneIds, pageable);
    }

    public Study validateStudyIsActive(String path) {
        Study study = findByPath(path);
        if (!study.isPublished() || study.isClosed()) {
            throw new InvalidStudyPublishStateException();
        }
        return study;
    }

    public Study validateStudyIsRecruiting(String path) {
        Study study = findByPath(path);
        if (!study.isPublished() || study.isClosed()) {
            throw new InvalidStudyPublishStateException();
        }
        if (!study.isRecruiting()) {
            throw new InvalidRecruitmentStateException();
        }
        return study;
    }

    public void remove(Long userId, String path) {
        Study study = findAuthorizedStudy(userId, path);
        studyRepository.delete(study);
    }

    public List<Study> getStudiesWithCond1Part1(StudySearchCond searchCond, Pageable pageable) {

        return studyQueryTestRepository.findAllStudiesWithNoCond(searchCond, pageable);
    }

    public PagedResponse<StudyQueryResponse> getStudiesWithCond(SearchCond searchCond, Pageable pageable) {
        Page<Study> page = studyQueryTest2Repository.findStudiesWithCondition(searchCond, pageable);
        List<StudyQueryResponse> studyQueryResponses = page.getContent()
                .stream()
                .map(StudyQueryResponse::of)
                .toList();

        return PagedResponse.<StudyQueryResponse>builder()
                .content(studyQueryResponses)
                .currentPage(pageable.getPageNumber() + 1) // 페이지는 0이 아닌 1페이지부터
                .totalPages(page.getTotalPages())
                .totalCount(page.getTotalElements())
                .size(pageable.getPageSize())
                .build();
    }

    // id 기반 쿼리
    public PagedResponse<StudyQueryResponse> getStudiesWithCond2(SearchCond searchCond, Pageable pageable) {
        Page<Study> page = studyQueryTest2Repository.findStudiesWithCondition2(searchCond, pageable);
        List<StudyQueryResponse> studyQueryResponses = page.getContent()
                .stream()
                .map(StudyQueryResponse::of)
                .toList();

        return PagedResponse.<StudyQueryResponse>builder()
                .content(studyQueryResponses)
                .currentPage(pageable.getPageNumber() + 1) // 페이지는 0이 아닌 1페이지부터
                .totalPages(page.getTotalPages())
                .totalCount(page.getTotalElements())
                .size(pageable.getPageSize())
                .build();
    }
}
