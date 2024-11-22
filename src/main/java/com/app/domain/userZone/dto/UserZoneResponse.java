package com.app.domain.userZone.dto;

import com.app.domain.userZone.UserZone;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter @Setter
@NoArgsConstructor
public class UserZoneResponse {

    private Set<Long> zoneIds;

    @Builder
    public UserZoneResponse(Set<Long> zoneIds) {
        this.zoneIds = zoneIds;
    }

    public static UserZoneResponse of(Set<UserZone> userZones) {
        Set<Long> zoneIds = userZones.stream()
                .map(userZone -> userZone.getZone().getId())
                .collect(Collectors.toSet());

        return UserZoneResponse.builder()
                .zoneIds(zoneIds)
                .build();
    }

}
