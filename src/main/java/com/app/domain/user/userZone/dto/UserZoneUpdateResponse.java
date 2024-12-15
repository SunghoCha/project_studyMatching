package com.app.domain.user.userZone.dto;

import com.app.domain.user.userZone.UserZone;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
public class UserZoneUpdateResponse {

    private List<ZoneDetailResponse> zones;

    @Builder
    public UserZoneUpdateResponse(List<ZoneDetailResponse> zones) {
        this.zones = zones;
    }

    public static UserZoneUpdateResponse of(List<UserZone> userZones) {
        List<ZoneDetailResponse> zones = userZones.stream()
                .map(userZone -> ZoneDetailResponse.of(userZone.getZone()))
                .toList();

        return UserZoneUpdateResponse.builder()
                .zones(zones)
                .build();
    }

}
