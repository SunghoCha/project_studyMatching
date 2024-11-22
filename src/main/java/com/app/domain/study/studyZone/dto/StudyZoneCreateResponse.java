package com.app.domain.study.studyZone.dto;

import com.app.domain.study.studyZone.StudyZone;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter @Setter
public class StudyZoneCreateResponse {

    private Set<Long> zoneIds;

    @Builder
    public StudyZoneCreateResponse(Set<Long> zoneIds) {
        this.zoneIds = zoneIds;
    }

    public static StudyZoneCreateResponse of(Set<StudyZone> studyZones) {
        Set<Long> studyZoneIds = studyZones.stream()
                .map(studyZone -> studyZone.getZone().getId())
                .collect(Collectors.toSet());

        return StudyZoneCreateResponse.builder()
                .zoneIds(studyZoneIds)
                .build();
    }
}
