package com.app.domain.study.studyZone.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter @Setter
public class StudyZoneCreateRequest {

    Set<Long> zoneIds;

    @Builder
    public StudyZoneCreateRequest(Set<Long> zoneIds) {
        this.zoneIds = zoneIds;
    }
}
