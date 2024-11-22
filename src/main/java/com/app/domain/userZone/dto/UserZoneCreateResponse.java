package com.app.domain.userZone.dto;

import com.app.domain.study.studyZone.dto.StudyZoneCreateResponse;
import com.app.domain.userZone.UserZone;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter @Setter
@NoArgsConstructor
public class UserZoneCreateResponse {

    private Set<Long> zoneIds;

    @Builder
    public UserZoneCreateResponse(Set<Long> zoneIds) {
        this.zoneIds = zoneIds;
    }

    public static UserZoneCreateResponse of(Set<UserZone> userZones) {
        Set<Long> zoneIds = userZones.stream()
                .map(userZone -> userZone.getZone().getId())
                .collect(Collectors.toSet());

        return UserZoneCreateResponse.builder()
                .zoneIds(zoneIds)
                .build();
    }

}
