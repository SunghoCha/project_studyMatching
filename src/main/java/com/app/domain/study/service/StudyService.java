package com.app.domain.study.service;

import com.app.domain.common.dto.PagedResponse;
import com.app.domain.study.Study;
import com.app.domain.study.StudyEditor;
import com.app.domain.study.dto.*;
import com.app.domain.study.dto.studySetting.StudyPathUpdateRequest;
import com.app.domain.study.dto.studySetting.StudyTitleUpdateRequest;
import com.app.domain.study.repository.StudyQueryRepository;
import com.app.domain.study.repository.StudyRepository;
import com.app.domain.study.studyManager.StudyManager;
import com.app.domain.study.studyManager.repository.StudyManagerRepository;
import com.app.domain.study.studyMember.StudyMember;
import com.app.domain.study.studyMember.repository.StudyMemberRepository;
import com.app.domain.user.User;
import com.app.domain.user.service.UserService;
import com.app.domain.userTag.UserTag;
import com.app.domain.userTag.service.UserTagService;
import com.app.domain.userZone.UserZone;
import com.app.domain.userZone.service.UserZoneService;
import com.app.global.error.exception.StudyMemberNotFoundException;
import com.app.global.error.exception.StudyNotFoundException;
import com.app.global.error.exception.StudyPathAlreadyExistException;
import com.app.global.error.exception.UnauthorizedAccessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StudyService {
    // TODO: 이벤트 알림 기능
    private final StudyRepository studyRepository;
    private final UserService userService;
    private final StudyQueryRepository studyQueryRepository;
    private final StudyManagerRepository studyManagerRepository;
    private final StudyMemberRepository studyMemberRepository;
    private final UserTagService userTagService;
    private final UserZoneService userZoneService;
    private final Clock clock;

    public StudyCreateResponse createStudy(Long userId, StudyCreateRequest request) {
        Optional<Study> existingStudy = studyRepository.findByPath(request.getPath());

        if (existingStudy.isPresent()) {
            throw new StudyPathAlreadyExistException();
        }
        // 스터디 저장 후 매니저 설정
        Study study = request.toEntity();
        User user = userService.getById(userId);
        StudyManager studyManager = studyManagerRepository.save(StudyManager.createManager(user, study));
        study.addManager(studyManager);
        Study savedStudy = studyRepository.save(study);

        return StudyCreateResponse.of(savedStudy);
    }

    public StudyResponse getStudy(Long userId, String path) {
        User user = userService.getById(userId);
        Study study = studyRepository.findByPath(path).orElseThrow(StudyNotFoundException::new);

        return StudyResponse.of(user, study);
    }

    public PagedResponse<StudyQueryResponse> getStudies(StudySearchCond searchCond, Pageable pageable) {
        return studyQueryRepository.findAllStudies(searchCond, pageable);
    }

    public PagedResponse<StudyQueryResponse> getMyManagedStudies(Long userId, Pageable pageable) {
        return studyQueryRepository.findMyManagedStudies(userId, pageable);
    }

    public PagedResponse<StudyQueryResponse> getMyJoinedStudies(Long userId, Pageable pageable) {
        return studyQueryRepository.findMyJoinedStudies(userId, pageable);
    }

    // TODO 성능 비교 후 삭제 예정
    public List<StudyResponse> getStudies2() {
        return studyRepository.findAll().stream()
                .map(StudyResponse::of)
                .toList();
    }

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

        return StudyUpdateResponse.of(study);
    }

    public Study findStudyWithManager(Long userId, String path) {
        User user = userService.getById(userId);
        Study study = studyRepository.findByStudyWithManagerByPath(path).orElseThrow(StudyNotFoundException::new);
        if (!study.isManager(user)) {
            throw new UnauthorizedAccessException();
        }
        return study;
    }

    public StudyResponse joinStudy(Long userId, String path) {
        User user = userService.getById(userId);
        Study study = studyRepository.findByStudyWithAllByPath(path).orElseThrow(StudyNotFoundException::new);

        StudyMember member = StudyMember.createMember(user, study);
        StudyMember saveMember = studyMemberRepository.save(member);
        study.addMember(saveMember);

        return StudyResponse.of(user, study);
    }

    public StudyResponse leaveStudy(Long userId, String path) {
        User user = userService.getById(userId);
        Study study = studyRepository.findByStudyWithAllByPath(path).orElseThrow(StudyNotFoundException::new);

        StudyMember member = studyMemberRepository.findByStudyAndUser(study, user).orElseThrow(StudyMemberNotFoundException::new);
        study.removeMember(member);
        studyMemberRepository.delete(member);

        return StudyResponse.of(user, study);
    }

    public Boolean publishStudy(Long userId, String path) {
        Study study = findAuthorizedStudy(userId, path);
        study.publish();

        return study.isPublished();
    }

    public Boolean closeStudy(Long userId, String path) {
        Study study = findAuthorizedStudy(userId, path);
        study.close();

        return study.isClosed();
    }

    public Boolean startRecruit(Long userId, String path) {
        Study study = findAuthorizedStudy(userId, path);
        study.startRecruit(LocalDateTime.now(clock));

        return study.isRecruiting();
    }

    public Boolean stopRecruit(Long userId, String path) {
        Study study = findAuthorizedStudy(userId, path);
        study.stopRecruit(LocalDateTime.now(clock));

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
}
