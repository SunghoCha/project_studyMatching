package com.app.domain.userZone.dto;

import com.app.domain.study.studyZone.StudyZone;
import com.app.domain.study.studyZone.dto.StudyZoneCreateResponse;
import com.app.domain.userZone.UserZone;
import lombok.*;

import java.util.Set;
import java.util.stream.Collectors;

@Getter @Setter
@NoArgsConstructor
public class UserZoneUpdateResponse {

    private Set<Long> zoneIds;

    @Builder
    public UserZoneUpdateResponse(Set<Long> zoneIds) {
        this.zoneIds = zoneIds;
    }

    public static UserZoneUpdateResponse of(Set<UserZone> userZones) {
        Set<Long> zoneIds = userZones.stream()
                .map(userZone -> userZone.getZone().getId())
                .collect(Collectors.toSet());

        return UserZoneUpdateResponse.builder()
                .zoneIds(zoneIds)
                .build();
    }

}
