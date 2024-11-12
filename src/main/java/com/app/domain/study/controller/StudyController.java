package com.app.domain.study.controller;

import com.app.domain.study.dto.StudyCreateRequest;
import com.app.domain.study.dto.StudyCreateResponse;
import com.app.domain.study.service.StudyService;
import com.app.global.config.auth.LoginUser;
import com.app.global.config.auth.dto.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


}
