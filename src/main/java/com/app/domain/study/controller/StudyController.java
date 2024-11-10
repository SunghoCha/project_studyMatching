package com.app.domain.study.controller;

import com.app.domain.study.dto.StudyCreateRequest;
import com.app.domain.study.service.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/study")
public class StudyController {

    private final StudyService studyService;

    @PostMapping("/new/{userId}")
    public ResponseEntity<StudyResponse> createStudy(@PathVariable(name = "userId") Long userId, @RequestBody StudyCreateRequest request) {

        return null;
    }
}
