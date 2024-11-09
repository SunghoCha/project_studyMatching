package com.app.domain.zone.dto;

import com.app.domain.zone.Zone;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ZoneRequest {

    private Long id;
    private String city;
    private String localName;
    private String province;

    public Zone toEntity() {
        return Zone.builder()
                .city(city)
                .localName(localName)
                .province(province)
                .build();
    }
}
