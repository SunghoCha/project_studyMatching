package com.app.domain.study.studyZone.dto;

import com.app.domain.study.studyZone.StudyZone;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter @Setter
public class StudyZoneUpdateResponse {

    private Set<Long> studyZoneIds;

    @Builder
    public StudyZoneUpdateResponse(Set<Long> studyZoneIds) {
        this.studyZoneIds = studyZoneIds;
    }

    public static StudyZoneUpdateResponse of(Set<StudyZone> studyZones) {
        Set<Long> studyZoneIds = studyZones.stream()
                .map(studyZone -> studyZone.getZone().getId())
                .collect(Collectors.toSet());

        return StudyZoneUpdateResponse.builder()
                .studyZoneIds(studyZoneIds)
                .build();
    }

}
