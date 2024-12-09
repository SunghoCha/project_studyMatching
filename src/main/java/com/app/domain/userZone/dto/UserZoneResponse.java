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

    private List<ZoneDetailResponse> zones;

    @Builder
    public UserZoneResponse(List<ZoneDetailResponse> zones) {
        this.zones = zones;
    }

    public static UserZoneResponse of(List<UserZone> userZones) {
        List<ZoneDetailResponse> zones = userZones.stream()
                .map(userZone -> ZoneDetailResponse.of(userZone.getZone()))
                .toList();

        return UserZoneResponse.builder()
                .zones(zones)
                .build();
    }

}
