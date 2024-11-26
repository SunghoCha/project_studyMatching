package com.app.domain.study.studyZone.service;

import com.app.domain.study.Study;
import com.app.domain.study.service.StudyService;
import com.app.domain.study.studyZone.StudyZone;
import com.app.domain.study.studyZone.dto.*;
import com.app.domain.study.studyZone.repository.StudyZoneRepository;
import com.app.domain.zone.Zone;
import com.app.domain.zone.service.ZoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyZoneService {

    private final StudyZoneRepository studyZoneRepository;
    private final StudyService studyService;
    private final ZoneService zoneService;

    public StudyZoneResponse getStudyZones(String path) {
        Study study = studyService.findByPath(path);
        List<StudyZone> studyZones = studyZoneRepository.findAllByStudy(study);

        return StudyZoneResponse.of(studyZones);
    }

    public StudyZoneCreateResponse createStudyZone(Long userId, String path, StudyZoneCreateRequest request) {
        Study study = studyService.findAuthorizedStudy(userId, path);

        List<StudyZone> studyZones = request.getZoneIds().stream()
                .map(zoneId -> {
                    Zone zone = zoneService.findById(zoneId);
                    return StudyZone.builder()
                            .study(study)
                            .zone(zone)
                            .build();
                })
                .toList();
        studyZoneRepository.saveAll(studyZones);
        study.addStudyZones(new HashSet<>(studyZones));

        return StudyZoneCreateResponse.of(studyZones);
    }

    public StudyZoneUpdateResponse updateStudyZone(Long userId, String path, StudyZoneUpdateRequest request) {
        Study study = studyService.findAuthorizedStudy(userId, path);
        // TODO :  한방쿼리로 수정 가능성 체크 , 이중루프 개선
        List<Zone> requestedZones = zoneService.findByIdIn(request.getZoneIds());
        Set<StudyZone> currentStudyZones = study.getStudyZones();

        // 기존 studyZone과 비교해서 추가, 삭제 대상목록 처리
        Set<StudyZone> studyZonesToRemove = currentStudyZones.stream()
                .filter(studyZone -> !requestedZones.contains(studyZone.getZone()))
                .collect(Collectors.toSet());

        Set<StudyZone> studyZonesToAdd = requestedZones.stream()
                .filter(zone -> currentStudyZones.stream().noneMatch(studyZone -> studyZone.getZone().equals(zone)))
                .map(zone -> StudyZone.builder()
                        .study(study)
                        .zone(zone)
                        .build())
                .collect(Collectors.toSet());

        studyZoneRepository.deleteAll(studyZonesToRemove);
        study.removeStudyZones(studyZonesToRemove);

        List<StudyZone> studyZones = studyZoneRepository.saveAll(studyZonesToAdd);
        study.addStudyZones(new HashSet<>(studyZones));

        return StudyZoneUpdateResponse.of(study.getStudyZones());
    }

    public StudyZoneDeleteResponse deleteStudyZone(Long userId, String path, StudyZoneDeleteRequest request) {
        Study study = studyService.findAuthorizedStudy(userId, path);
        List<StudyZone> studyZones = studyZoneRepository.findByStudyAndZoneIds(study.getId(), request.getZoneIds());

        study.removeStudyZones(new HashSet<>(studyZones));
        studyZoneRepository.deleteAll(studyZones);

        return StudyZoneDeleteResponse.of(studyZones);
    }
}
