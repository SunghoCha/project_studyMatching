package com.app.domain.userZone.dto;

import com.app.domain.userZone.UserZone;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
public class UserZoneUpdateResponse {

    private List<Long> zoneIds;

    @Builder
    public UserZoneUpdateResponse(List<Long> zoneIds) {
        this.zoneIds = zoneIds;
    }

    public static UserZoneUpdateResponse of(List<UserZone> userZones) {
        List<Long> zoneIds = userZones.stream()
                .map(userZone -> userZone.getZone().getId())
                .toList();

        return UserZoneUpdateResponse.builder()
                .zoneIds(zoneIds)
                .build();
    }

}
