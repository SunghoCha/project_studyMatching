package com.app.domain.study.studyZone.service;

import com.app.domain.study.Study;
import com.app.domain.study.service.StudyService;
import com.app.domain.study.studyZone.StudyZone;
import com.app.domain.study.studyZone.dto.StudyZoneCreateRequest;
import com.app.domain.study.studyZone.dto.StudyZoneCreateResponse;
import com.app.domain.study.studyZone.dto.StudyZoneUpdateRequest;
import com.app.domain.study.studyZone.dto.StudyZoneUpdateResponse;
import com.app.domain.study.studyZone.repository.StudyZoneRepository;
import com.app.domain.zone.Zone;
import com.app.domain.zone.service.ZoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyZoneService {

    private final StudyZoneRepository studyZoneRepository;
    private final StudyService studyService;
    private final ZoneService zoneService;

    public StudyZoneCreateResponse createStudyZone(Long userId, String path, StudyZoneCreateRequest request) {
        Study study = studyService.findAuthorizedStudy(userId, path);
        Set<StudyZone> studyZones = request.getZoneIds().stream()
                .map(zoneId -> {
                    Zone zone = zoneService.findById(zoneId);
                    return StudyZone.builder()
                            .study(study)
                            .zone(zone)
                            .build();
                })
                .collect(Collectors.toSet());
        studyZoneRepository.saveAll(studyZones);
        study.addStudyZones(studyZones);

        return StudyZoneCreateResponse.of(studyZones);
    }

    public StudyZoneUpdateResponse updateStudyZone(Long id, String path, StudyZoneUpdateRequest request) {
        Study study = studyService.findAuthorizedStudy(id, path);
        Set<StudyZone> studyZones = request.getZoneIds().stream()
                .map(zoneId -> {
                    Zone zone = zoneService.findById(zoneId);
                    return StudyZone.builder()
                            .study(study)
                            .zone(zone)
                            .build();
                })
                .collect(Collectors.toSet());
        studyZoneRepository.saveAll(studyZones);
        study.addStudyZones(studyZones);

        return StudyZoneUpdateResponse.of(studyZones);
    }
}
