package com.app.domain.study.studyTag.controller;

import com.app.domain.study.studyTag.dto.*;
import com.app.domain.study.studyTag.service.StudyTagService;
import com.app.global.config.auth.LoginUser;
import com.app.global.config.auth.dto.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/study-tag/{path}")
public class StudyTagController {

    private final StudyTagService studyTagService;

    @GetMapping
    public ResponseEntity<StudyTagResponse> getStudyTags(@PathVariable("path") String path) {
        StudyTagResponse response = studyTagService.getStudyTags(path);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<StudyTagCreateResponse> createStudyTags(@LoginUser CurrentUser currentUser, @PathVariable("path") String path,
                                                                 @RequestBody StudyTagCreateRequest request) {
        StudyTagCreateResponse response = studyTagService.createStudyTags(currentUser.getId(), path, request);

        return ResponseEntity.ok(response);
    }

    @PatchMapping
    public ResponseEntity<StudyTagUpdateResponse> updateStudyTags(@LoginUser CurrentUser currentUser, @PathVariable("path") String path,
                                                                  @RequestBody StudyTagUpdateRequest request) {
        StudyTagUpdateResponse response = studyTagService.updateStudyTags(currentUser.getId(), path, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<Long> deleteStudyTags(@LoginUser CurrentUser currentUser, @PathVariable("path") String path) {
        // TODO: 태그 전체 삭제 로직
        Long deletedCount = studyTagService.deleteAll(currentUser.getId(), path);

        return ResponseEntity.ok(deletedCount);
    }


}
