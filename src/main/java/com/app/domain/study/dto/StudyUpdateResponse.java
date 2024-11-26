package com.app.domain.study.dto;

import com.app.domain.study.Study;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class StudyUpdateResponse {

    private String path;
    private String title;
    private String shortDescription;

    @Builder
    public StudyUpdateResponse(String path, String title, String shortDescription) {
        this.path = path;
        this.title = title;
        this.shortDescription = shortDescription;
    }

    public static StudyUpdateResponse of(Study study) {

        return StudyUpdateResponse.builder()
                .path(study.getPath())
                .title(study.getTitle())
                .shortDescription(study.getShortDescription())
                .build();
    }
}
