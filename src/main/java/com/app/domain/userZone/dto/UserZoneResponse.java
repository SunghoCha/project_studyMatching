package com.app.domain.userZone.dto;

import com.app.domain.user.User;
import com.app.domain.userZone.UserZone;
import com.app.domain.zone.Zone;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserZoneResponse {

    private String city;
    private String localName;
    private String province;

    @Builder
    public UserZoneResponse(String city, String localName, String province) {
        this.city = city;
        this.localName = localName;
        this.province = province;
    }

    public static UserZoneResponse of(UserZone userZone) {
        // TODO 쿼리 최적화
        return UserZoneResponse.builder()
                .city(userZone.getZone().getCity())
                .localName(userZone.getZone().getLocalName())
                .province(userZone.getZone().getProvince())
                .build();
    }
}
