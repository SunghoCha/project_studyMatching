package com.app.domain.study.studyZone.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class StudyZoneUpdateRequest {

    List<Long> zoneIds;

    @Builder
    public StudyZoneUpdateRequest(List<Long> zoneIds) {
        this.zoneIds = zoneIds;
    }
}
