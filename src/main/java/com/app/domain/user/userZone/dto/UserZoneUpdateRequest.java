package com.app.domain.user.userZone.dto;

import lombok.*;

import java.util.List;
import java.util.Set;

@Getter @Setter
@NoArgsConstructor
public class UserZoneUpdateRequest {

    private Set<Long> zoneIds;

    @Builder
    public UserZoneUpdateRequest(Set<Long> zoneIds) {
        this.zoneIds = zoneIds;
    }
}
