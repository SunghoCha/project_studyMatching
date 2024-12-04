package com.app.domain.study.controller;

import com.app.domain.study.dto.studySetting.StudyPathUpdateRequest;
import com.app.domain.study.dto.studySetting.StudyTitleUpdateRequest;
import com.app.domain.study.service.StudyService;
import com.app.global.config.auth.LoginUser;
import com.app.global.config.auth.dto.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/study/{path}/setting")
@RequiredArgsConstructor
public class StudySettingController {

    private final StudyService studyService;

    @PostMapping("/publish")
    public ResponseEntity<Map<String, Boolean>> publishStudy(@LoginUser CurrentUser currentUser, @PathVariable("path") String path) {
        Boolean isPublished = studyService.publishStudy(currentUser.getId(), path);
        Map<String, Boolean> response = Map.of("published", isPublished);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/close")
    public ResponseEntity<Map<String, Boolean>> closeStudy(@LoginUser CurrentUser currentUser, @PathVariable("path") String path) {
        Boolean isClosed = studyService.closeStudy(currentUser.getId(), path);
        Map<String, Boolean> response = Map.of("closed", isClosed);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/recruit/start")
    public ResponseEntity<Map<String, Boolean>> startRecruit(@LoginUser CurrentUser currentUser, @PathVariable("path") String path) {
        Boolean isRecruiting = studyService.startRecruit(currentUser.getId(), path);
        Map<String, Boolean> response = Map.of("recruiting", isRecruiting);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/recruit/stop")
    public ResponseEntity<Map<String, Boolean>> stopRecruit(@LoginUser CurrentUser currentUser, @PathVariable("path") String path) {
        Boolean isRecruiting = studyService.stopRecruit(currentUser.getId(), path);
        Map<String, Boolean> response = Map.of("recruiting", isRecruiting);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/path")
    public ResponseEntity<Map<String, String>> updateStudyPath(@LoginUser CurrentUser currentUser,
                                                               @PathVariable("path") String path,
                                                               @RequestBody StudyPathUpdateRequest request) {
        String newPath = studyService.updateStudyPath(currentUser.getId(), path, request);
        Map<String, String> response = Map.of("path", newPath);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/title")
    public ResponseEntity<Map<String, String>> updateStudyTitle(@LoginUser CurrentUser currentUser,
                                                                @PathVariable("path") String path,
                                                                @RequestBody StudyTitleUpdateRequest request) {
        String newTitle = studyService.updateStudyTitle(currentUser.getId(), path, request);
        Map<String, String> response = Map.of("title", newTitle);

        return ResponseEntity.ok(response);
    }


}
