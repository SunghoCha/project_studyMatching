package com.app.domain.userZone.dto;

import lombok.*;

import java.util.Set;

@Getter @Setter
@NoArgsConstructor
public class UserZoneUpdateRequest {

    private Long id;
    private String city;
    private String localName;
    private String province;

    @Builder
    public UserZoneUpdateRequest(Long id, String city, String localName, String province) {
        this.id = id;
        this.city = city;
        this.localName = localName;
        this.province = province;
    }
}
