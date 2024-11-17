package com.app.domain.study.service;

import com.app.domain.study.Study;
import com.app.domain.study.dto.StudyCreateRequest;
import com.app.domain.study.dto.StudyCreateResponse;
import com.app.domain.study.dto.StudyResponse;
import com.app.domain.study.repository.StudyRepository;
import com.app.domain.user.User;
import com.app.domain.user.repository.UserRepository;
import com.app.domain.user.service.UserService;
import com.app.global.config.auth.dto.CurrentUser;
import com.app.global.error.exception.StudyNotFoundException;
import com.app.global.error.exception.StudyPathAlreadyExistException;
import com.app.global.error.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository studyRepository;
    private final UserService userService;

    public StudyCreateResponse createStudy(Long userId, StudyCreateRequest request) {
        Optional<Study> existingStudy = studyRepository.findByPath(request.getPath());

        if (existingStudy.isPresent()) {
            throw new StudyPathAlreadyExistException();
        }
        // 스터디 저장 후 매니저 설정
        Study savedStudy = studyRepository.save(request.toEntity());
        User user = userService.getById(userId);
        savedStudy.addManager(user);

        return StudyCreateResponse.of(savedStudy);
    }

    public StudyResponse getStudy(Long userId, String path) {
        User user = userService.getById(userId);
        Study study = studyRepository.findByPath(path).orElseThrow(StudyNotFoundException::new);

        return StudyResponse.of(user, study);
    }

    public List<StudyResponse> getStudies() {
        return studyRepository.findAll().stream()
                .map(StudyResponse::of)
                .toList();
    }
}
