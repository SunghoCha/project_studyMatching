package com.app.domain.study.studyZone.dto;

import com.app.domain.study.studyZone.StudyZone;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter @Setter
public class StudyZoneUpdateResponse {

    private Set<Long> zoneIds;

    @Builder
    public StudyZoneUpdateResponse(Set<Long> zoneIds) {
        this.zoneIds = zoneIds;
    }

    public static StudyZoneUpdateResponse of(Set<StudyZone> studyZones) {
        Set<Long> zoneIds = studyZones.stream()
                .map(studyZone -> studyZone.getZone().getId())
                .collect(Collectors.toSet());

        return StudyZoneUpdateResponse.builder()
                .zoneIds(zoneIds)
                .build();
    }

}
