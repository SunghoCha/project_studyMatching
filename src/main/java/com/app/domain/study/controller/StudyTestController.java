package com.app.domain.study.controller;

import com.app.domain.common.dto.PagedResponse;
import com.app.domain.study.Study;
import com.app.domain.study.dto.*;
import com.app.domain.study.service.StudyService;
import com.app.domain.study.service.StudyTestService;
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
@RequestMapping("/study-test")
public class StudyTestController {

    private final StudyTestService studyTestService;

    @GetMapping("/list-cond1-part1")
    public ResponseEntity<Void> getStudiesWithCond1Part1(StudySearchCond searchCond, Pageable pageable) {
        log.info("getStudiesWithCond1Part1 호출");
        List<Study> studies = studyTestService.getStudiesWithCond1Part1(searchCond, pageable);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search-cond1")
    public ResponseEntity<PagedResponse<StudyQueryResponse>> getStudiesWithCond(SearchCond searchCond, Pageable pageable) {
        PagedResponse<StudyQueryResponse> response = studyTestService.getStudiesWithCond(searchCond, pageable);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search-cond2")
    public ResponseEntity<PagedResponse<StudyQueryResponse>> getStudiesWithCond2(SearchCond searchCond, Pageable pageable) {
        PagedResponse<StudyQueryResponse> response = studyTestService.getStudiesWithCond2(searchCond, pageable);

        return ResponseEntity.ok(response);
    }



















}
