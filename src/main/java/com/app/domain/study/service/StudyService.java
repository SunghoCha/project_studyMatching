package com.app.domain.study.service;

import com.app.domain.common.dto.PagedResponse;
import com.app.domain.study.Study;
import com.app.domain.study.dto.StudyCreateRequest;
import com.app.domain.study.dto.StudyCreateResponse;
import com.app.domain.study.dto.StudyQueryResponse;
import com.app.domain.study.dto.StudyResponse;
import com.app.domain.study.repository.StudyQueryRepository;
import com.app.domain.study.repository.StudyRepository;
import com.app.domain.study.studyManager.StudyManager;
import com.app.domain.study.studyManager.repository.StudyManagerRepository;
import com.app.domain.user.User;
import com.app.domain.user.service.UserService;
import com.app.global.error.exception.StudyNotFoundException;
import com.app.global.error.exception.StudyPathAlreadyExistException;
import com.app.global.error.exception.UnauthorizedAccessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository studyRepository;
    private final UserService userService;
    private final StudyQueryRepository studyQueryRepository;
    private final StudyManagerRepository studyManagerRepository;

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

    public PagedResponse<StudyQueryResponse> getStudies(Pageable pageable) {
        return studyQueryRepository.findAllStudies(pageable);
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
}
