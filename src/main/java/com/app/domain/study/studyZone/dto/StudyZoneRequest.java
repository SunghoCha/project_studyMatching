package com.app.domain.study.studyZone.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class StudyZoneRequest {

    private Long id;
    private String city;
    private String localName;
    private String province;

    @Builder
    public StudyZoneRequest(Long id, String city, String localName, String province) {
        this.id = id;
        this.city = city;
        this.localName = localName;
        this.province = province;
    }
}
