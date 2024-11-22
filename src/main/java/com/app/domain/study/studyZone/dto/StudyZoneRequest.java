package com.app.domain.study.studyZone.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter @Setter
public class StudyZoneRequest {

    private Set<Long> zoneIds;

    @Builder
    public StudyZoneRequest(Set<Long> zoneIds) {
        this.zoneIds = zoneIds;
    }
}
