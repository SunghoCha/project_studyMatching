package com.app.domain.study.studyZone.controller;

import com.app.domain.study.studyZone.dto.StudyZoneCreateRequest;
import com.app.domain.study.studyZone.dto.StudyZoneCreateResponse;
import com.app.domain.study.studyZone.dto.StudyZoneResponse;
import com.app.domain.study.studyZone.dto.StudyZoneUpdateRequest;
import com.app.domain.study.studyZone.service.StudyZoneService;
import com.app.global.config.auth.LoginUser;
import com.app.global.config.auth.dto.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/study-zone")
public class StudyZoneController {

    private final StudyZoneService studyZoneService;

    @GetMapping("/{path}")
    public StudyZoneResponse getStudyZones() {

        return null;
    }

    @PostMapping("/{path}")
    public ResponseEntity<StudyZoneCreateResponse> createStudyZones(@LoginUser CurrentUser currentUser,
                                                                    @PathVariable("path") String path,
                                                                    @RequestBody StudyZoneCreateRequest request) {
        StudyZoneCreateResponse response = studyZoneService.createStudyZone(currentUser.getId(), path, request);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{path}")
    public StudyZoneResponse updateStudyZones(@LoginUser CurrentUser currentUser,
                                            @PathVariable("path") String path,
                                            @RequestBody StudyZoneUpdateRequest request) {
        studyZoneService.updateStudyZone(currentUser.getId(), path, request);

        return null;
    }

    @DeleteMapping("/{path}")
    public StudyZoneResponse deleteStudyZones() {

        return null;
    }
}
