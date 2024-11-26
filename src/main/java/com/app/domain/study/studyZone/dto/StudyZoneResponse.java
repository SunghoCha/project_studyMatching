package com.app.domain.study.studyZone.dto;

import com.app.domain.study.studyZone.StudyZone;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class StudyZoneResponse {

    private List<Long> zoneIds;

    @Builder
    public StudyZoneResponse(List<Long> zoneIds) {
        this.zoneIds = zoneIds;
    }

    public static StudyZoneResponse of(List<StudyZone> studyZones) {
        List<Long> zoneIds = studyZones.stream()
                .map(studyZone -> studyZone.getZone().getId())
                .toList();

        return StudyZoneResponse.builder()
                .zoneIds(zoneIds)
                .build();
    }
}
