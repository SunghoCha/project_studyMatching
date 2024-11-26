package com.app.domain.userZone.dto;

import com.app.domain.userZone.UserZone;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter @Setter
@NoArgsConstructor
public class UserZoneCreateResponse {

    private List<Long> zoneIds;

    @Builder
    public UserZoneCreateResponse(List<Long> zoneIds) {
        this.zoneIds = zoneIds;
    }

    public static UserZoneCreateResponse of(Set<UserZone> userZones) {
        List<Long> zoneIds = userZones.stream()
                .map(userZone -> userZone.getZone().getId())
                .toList();

        return UserZoneCreateResponse.builder()
                .zoneIds(zoneIds)
                .build();
    }

}
