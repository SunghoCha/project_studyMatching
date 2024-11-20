package com.app.domain.study.studyTag.service;

import com.app.domain.study.Study;
import com.app.domain.study.service.StudyService;
import com.app.domain.study.studyTag.StudyTag;
import com.app.domain.study.studyTag.dto.StudyTagCreateRequest;
import com.app.domain.study.studyTag.dto.StudyTagCreateResponse;
import com.app.domain.study.studyTag.repository.StudyTagRepository;
import com.app.domain.tag.Tag;
import com.app.domain.tag.service.TagService;
import com.app.domain.user.User;
import com.app.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyTagService {

    private final StudyTagRepository studyTagRepository;
    private final StudyService studyService;
    private final TagService tagService;

    public StudyTagCreateResponse addStudyTags(Long userId, String path, StudyTagCreateRequest request) {
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
        studyTagRepository.saveAll(studyTags);
        study.setStudyTags(studyTags);


        return StudyTagCreateResponse.of(studyTags);
    }
}
