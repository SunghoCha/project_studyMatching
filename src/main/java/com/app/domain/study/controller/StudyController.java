package com.app.domain.study.controller;

import com.app.domain.common.dto.PagedResponse;
import com.app.domain.study.dto.*;
import com.app.domain.study.service.StudyService;
import com.app.global.config.auth.LoginUser;
import com.app.global.config.auth.dto.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/study")
public class StudyController {

    private final StudyService studyService;

    @PostMapping("/new")
    public ResponseEntity<StudyCreateResponse> createStudy(@LoginUser CurrentUser currentUser, @Valid @RequestBody StudyCreateRequest request) {
        StudyCreateResponse response = studyService.createStudy(currentUser.getId(), request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{path}")
    public ResponseEntity<StudyResponse> getStudy(@LoginUser CurrentUser currentUser, @PathVariable("path") String path) {
        StudyResponse response = studyService.getStudy(currentUser.getId(), path);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    public ResponseEntity<PagedResponse<StudyQueryResponse>> getStudies(Pageable pageable) {
        PagedResponse<StudyQueryResponse> response = studyService.getStudies(pageable);
        log.info("response: {}", response);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{path}")
    public ResponseEntity<StudyUpdateResponse> updateStudy(@LoginUser CurrentUser currentUser,
                                                           @PathVariable("path") String path,
                                                           @RequestBody StudyUpdateRequest request) {

        StudyUpdateResponse response = studyService.updateStudy(currentUser.getId(), path, request);
        return null;
    }
}
