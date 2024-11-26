package com.app.domain.study.studyZone.dto;

import com.app.domain.study.studyZone.StudyZone;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter @Setter
public class StudyZoneUpdateResponse {

    private List<Long> zoneIds;

    @Builder
    public StudyZoneUpdateResponse(List<Long> zoneIds) {
        this.zoneIds = zoneIds;
    }

    public static StudyZoneUpdateResponse of(Set<StudyZone> studyZones) {
        List<Long> zoneIds = studyZones.stream()
                .sorted(Comparator.comparing(studyZone -> studyZone.getZone().getId()))
                .map(studyZone -> studyZone.getZone().getId())
                .toList();

        return StudyZoneUpdateResponse.builder()
                .zoneIds(zoneIds)
                .build();
    }
}
