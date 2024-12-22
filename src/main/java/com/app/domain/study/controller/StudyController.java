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
    public ResponseEntity<PagedResponse<StudyQueryResponse>> getStudies(StudySearchCond searchCond, Pageable pageable) {
        log.info("스터디 목록 요청: 검색 조건 = {}, 페이지 정보 = {}", searchCond, pageable);
        PagedResponse<StudyQueryResponse> response = studyService.getStudies(searchCond,pageable);
        log.info("스터디 목록 반환 성공: 총 {}건", response.getTotalCount());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-managed-study-list")
    public ResponseEntity<PagedResponse<StudyQueryResponse>> getMyManagedStudies(@LoginUser CurrentUser currentUser, Pageable pageable) {
        PagedResponse<StudyQueryResponse> response = studyService.getMyManagedStudies(currentUser.getId(), pageable);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-joined-study-list")
    public ResponseEntity<PagedResponse<StudyQueryResponse>> getMyJoinedStudies(@LoginUser CurrentUser currentUser, Pageable pageable) {
        PagedResponse<StudyQueryResponse> response = studyService.getMyJoinedStudies(currentUser.getId(), pageable);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-study-wishlist")
    public ResponseEntity<PagedResponse<StudyQueryResponse>> getStudyWishlist(@LoginUser CurrentUser currentUser, Pageable pageable) {
        PagedResponse<StudyQueryResponse> response = studyService.getStudyWishlist(currentUser.getId(), pageable);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/description/{path}")
    public ResponseEntity<StudyUpdateResponse> updateStudy(@LoginUser CurrentUser currentUser,
                                                           @PathVariable("path") String path,
                                                           @RequestBody StudyUpdateRequest request) {
        StudyUpdateResponse response = studyService.updateDescription(currentUser.getId(), path, request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{path}/join")
    public ResponseEntity<StudyResponse> joinStudy(@LoginUser CurrentUser currentUser,
                                                   @PathVariable("path") String path) {
        StudyResponse response = studyService.joinStudy(currentUser.getId(), path);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{path}/leave")
    public ResponseEntity<StudyResponse> leaveStudy(@LoginUser CurrentUser currentUser,
                                                    @PathVariable("path") String path) {
        StudyResponse response = studyService.leaveStudy(currentUser.getId(), path);

        return ResponseEntity.ok(response);
    }
}
