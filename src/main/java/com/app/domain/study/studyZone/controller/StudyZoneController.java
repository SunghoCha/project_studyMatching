package com.app.domain.study.studyZone.controller;

import com.app.domain.study.studyZone.dto.*;
import com.app.domain.study.studyZone.service.StudyZoneService;
import com.app.global.config.auth.LoginUser;
import com.app.global.config.auth.dto.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/study-zone/{path}")
public class StudyZoneController {

    private final StudyZoneService studyZoneService;

    @GetMapping
    public ResponseEntity<StudyZoneResponse> getStudyZones(@PathVariable("path") String path) {
        StudyZoneResponse response = studyZoneService.getStudyZones(path);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<StudyZoneCreateResponse> createStudyZones(@LoginUser CurrentUser currentUser,
                                                                    @PathVariable("path") String path,
                                                                    @RequestBody StudyZoneCreateRequest request) {
        StudyZoneCreateResponse response = studyZoneService.createStudyZone(currentUser.getId(), path, request);

        return ResponseEntity.ok(response);
    }

    @PatchMapping
    public ResponseEntity<StudyZoneUpdateResponse> updateStudyZones(@LoginUser CurrentUser currentUser,
                                            @PathVariable("path") String path,
                                            @RequestBody StudyZoneUpdateRequest request) {
        StudyZoneUpdateResponse response = studyZoneService.updateStudyZone(currentUser.getId(), path, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteStudyZones(@LoginUser CurrentUser currentUser,
                                                              @PathVariable("path") String path,
                                                              @RequestBody StudyZoneDeleteRequest request) {
         studyZoneService.deleteStudyZone(currentUser.getId(), path, request);

        return ResponseEntity.noContent().build();
    }
}
