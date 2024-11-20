package com.app.domain.userZone.dto;

import com.app.domain.userZone.UserZone;
import lombok.*;

@Getter @Setter
@EqualsAndHashCode(of = {"city", "localName", "province"})
@NoArgsConstructor
public class UserZoneUpdateResponse {

    private String city;
    private String localName;
    private String province;

    @Builder
    public UserZoneUpdateResponse(String city, String localName, String province) {
        this.city = city;
        this.localName = localName;
        this.province = province;
    }

    public static UserZoneUpdateResponse of(UserZone userZone) {
        // TODO 쿼리 최적화
        return UserZoneUpdateResponse.builder()
                .city(userZone.getZone().getCity())
                .localName(userZone.getZone().getLocalName())
                .province(userZone.getZone().getProvince())
                .build();
    }
}
