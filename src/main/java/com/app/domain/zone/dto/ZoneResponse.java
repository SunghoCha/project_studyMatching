package com.app.domain.zone.dto;

import com.app.domain.zone.Zone;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@EqualsAndHashCode
public class ZoneResponse {

    private Long id;
    private String city;
    private String localName;
    private String province;

    @Builder
    @QueryProjection
    public ZoneResponse(Long id, String city, String localName, String province) {
        this.id = id;
        this.city = city;
        this.localName = localName;
        this.province = province;
    }

    public static ZoneResponse of(Zone zone) {
        return ZoneResponse.builder()
                .id(zone.getId())
                .city(zone.getCity())
                .localName(zone.getLocalName())
                .province(zone.getProvince())
                .build();
    }
}
