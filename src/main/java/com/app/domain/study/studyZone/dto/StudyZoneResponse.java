package com.app.domain.study.studyZone.dto;

import com.app.domain.study.studyZone.StudyZone;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.stream.Collectors;

@Getter @Setter
public class StudyZoneResponse {

    private Set<Long> zoneIds;

    @Builder
    public StudyZoneResponse(Set<Long> zoneIds) {
        this.zoneIds = zoneIds;
    }

    public static StudyZoneResponse of(Set<StudyZone> studyZones) {
        Set<Long> zoneIds = studyZones.stream()
                .map(studyZone -> studyZone.getZone().getId())
                .collect(Collectors.toSet());

        return StudyZoneResponse.builder()
                .zoneIds(zoneIds)
                .build();
    }
}
