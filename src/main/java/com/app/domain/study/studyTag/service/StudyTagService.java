package com.app.domain.study.studyTag.service;

import com.app.domain.study.Study;
import com.app.domain.study.service.StudyService;
import com.app.domain.study.studyTag.StudyTag;
import com.app.domain.study.studyTag.dto.*;
import com.app.domain.study.studyTag.repository.StudyTagRepository;
import com.app.domain.tag.Tag;
import com.app.domain.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyTagService {

    private final StudyTagRepository studyTagRepository;
    private final StudyService studyService;
    private final TagService tagService;

    public StudyTagCreateResponse createStudyTags(Long userId, String path, StudyTagCreateRequest request) {
        Study study = studyService.findAuthorizedStudy(userId, path);
        Set<StudyTag> studyTags = request.getTags().stream()
                .map(tagTitle -> {
                    Tag tag = tagService.findByTitle(tagTitle);
                    return StudyTag.builder()
                            .study(study)
                            .tag(tag)
                            .build();
                })
                .collect(Collectors.toSet());
        List<StudyTag> savedTags = studyTagRepository.saveAll(studyTags);

        study.addStudyTags(new HashSet<>(savedTags));

        return StudyTagCreateResponse.of(savedTags);
    }

    public StudyTagUpdateResponse updateStudyTags(Long userId, String path, StudyTagUpdateRequest request) {
        Study study = studyService.findAuthorizedStudy(userId, path);
        // TODO : title 한방쿼리로 수정, 이중루프 개선
        Set<Tag> requestedTags = request.getTags().stream()
                .map(tagService::findByTitle)
                .collect(Collectors.toSet());

        Set<StudyTag> currentStudyTags = study.getStudyTags();

        // 기존 태그와 요청된 태그 비교해서 추가, 삭제 태그목록 처리
        Set<StudyTag> tagsToRemove = currentStudyTags.stream()
                .filter(studyTag -> !requestedTags.contains(studyTag.getTag()))
                .collect(Collectors.toSet());

        Set<StudyTag> tagsToAdd = requestedTags.stream()
                .filter(tag -> currentStudyTags.stream().noneMatch(studyTag -> studyTag.getTag().equals(tag)))
                .map(tag -> StudyTag.builder()
                        .study(study)
                        .tag(tag)
                        .build())
                .collect(Collectors.toSet());

        studyTagRepository.deleteAll(tagsToRemove);
        study.removeStudyTags(tagsToRemove);

        List<StudyTag> studyTags = studyTagRepository.saveAll(tagsToAdd);
        study.addStudyTags(new HashSet<>(studyTags));

        return StudyTagUpdateResponse.of(study.getStudyTags());
    }

    public Long deleteAll(Long userId, String path) {
        Study study = studyService.findAuthorizedStudy(userId, path);
        return studyTagRepository.deleteAllInStudy(study);
    }

    public StudyTagResponse getStudyTags(String path) {
        List<StudyTag> studyTags = studyTagRepository.findAllByStudyPath(path);

        return StudyTagResponse.of(studyTags);
    }
}
