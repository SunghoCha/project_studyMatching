package com.app.domain.study.controller;

import com.app.domain.study.dto.StudyCreateRequest;
import com.app.domain.study.dto.StudyCreateResponse;
import com.app.domain.study.dto.StudyResponse;
import com.app.domain.study.service.StudyService;
import com.app.global.config.auth.LoginUser;
import com.app.global.config.auth.dto.CurrentUser;
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
    public ResponseEntity<StudyCreateResponse> createStudy(@LoginUser CurrentUser currentUser, @RequestBody StudyCreateRequest request) {
        StudyCreateResponse response = studyService.createStudy(currentUser.getId(), request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{path}")
    public ResponseEntity<StudyResponse> getStudy(@LoginUser CurrentUser currentUser, @PathVariable("path") String path) {
        StudyResponse response = studyService.getStudy(currentUser.getId(), path);

        return ResponseEntity.ok(response);

    }

    @GetMapping("/list")
    public ResponseEntity<List<StudyResponse>> getStudies(Pageable pageable) {
        List<StudyResponse> response = studyService.getStudies(pageable);
        log.info("response: {}", response);

        return ResponseEntity.ok(response);
    }
}
