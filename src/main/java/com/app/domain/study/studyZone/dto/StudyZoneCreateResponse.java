package com.app.domain.study.studyZone.dto;

import com.app.domain.study.studyZone.StudyZone;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter @Setter
public class StudyZoneCreateResponse {

    private List<Long> zoneIds;

    @Builder
    public StudyZoneCreateResponse(List<Long> zoneIds) {
        this.zoneIds = zoneIds;
    }

    public static StudyZoneCreateResponse of(List<StudyZone> studyZones) {
        List<Long> studyZoneIds = studyZones.stream()
                .map(studyZone -> studyZone.getZone().getId())
                .toList();

        return StudyZoneCreateResponse.builder()
                .zoneIds(studyZoneIds)
                .build();
    }
}
