package com.app.domain.userZone.dto;

import com.app.domain.userZone.UserZone;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
public class UserZoneResponse {

    private List<Long> zoneIds;

    @Builder
    public UserZoneResponse(List<Long> zoneIds) {
        this.zoneIds = zoneIds;
    }

    public static UserZoneResponse of(List<UserZone> userZones) {
        List<Long> zoneIds = userZones.stream()
                .map(userZone -> userZone.getZone().getId())
                .toList();

        return UserZoneResponse.builder()
                .zoneIds(zoneIds)
                .build();
    }

}
