package com.app.domain.study.studyTag.controller;

import com.app.domain.study.studyTag.dto.StudyTagCreateRequest;
import com.app.domain.study.studyTag.dto.StudyTagCreateResponse;
import com.app.domain.study.studyTag.dto.StudyTagUpdateRequest;
import com.app.domain.study.studyTag.service.StudyTagService;
import com.app.global.config.auth.LoginUser;
import com.app.global.config.auth.dto.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/study-tag")
public class StudyTagController {

    private final StudyTagService studyTagService;

    @PostMapping("/{path}")
    public ResponseEntity<StudyTagCreateResponse> createStudyTags(@LoginUser CurrentUser currentUser, @PathVariable("path") String path,
                                                                 @RequestBody StudyTagCreateRequest request) {
        StudyTagCreateResponse response = studyTagService.createStudyTags(currentUser.getId(), path, request);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{path}")
    public void editStudyTags(@LoginUser CurrentUser currentUser, @PathVariable("path") String path,
                              @RequestBody StudyTagUpdateRequest request) {
        // TODO: 태그 수정 로직
    }

    @DeleteMapping("/{path}")
    public void deleteStudyTags(@LoginUser CurrentUser currentUser, @PathVariable("path") String path) {
        // TODO: 태그 전체 삭제 로직
    }


}
