package com.app.domain.study.studyZone.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter @Setter
@NoArgsConstructor
public class StudyZoneUpdateRequest {

    private Set<Long> zoneIds;

    @Builder
    public StudyZoneUpdateRequest(Set<Long> zoneIds) {
        this.zoneIds = zoneIds;
    }
}
