package com.app.domain.study.studyZone.dto;

import com.app.domain.study.studyZone.StudyZone;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter @Setter
public class StudyZoneDeleteResponse {

    private List<Long> zoneIds;

    @Builder
    public StudyZoneDeleteResponse(List<Long> zoneIds) {
        this.zoneIds = zoneIds;
    }

    public static StudyZoneDeleteResponse of(List<StudyZone> studyZones) {
        List<Long> zoneIds = studyZones.stream()
                .map(studyZone -> studyZone.getZone().getId())
                .toList();

        return StudyZoneDeleteResponse.builder()
                .zoneIds(zoneIds)
                .build();
    }
}
