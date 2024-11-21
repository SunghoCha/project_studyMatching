package com.app.domain.study.dto;

import com.app.domain.study.Study;
import com.app.domain.tag.dto.TagResponse;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class StudyQueryResponse {

    private String path;
    private String title;
    private String shortDescription;
    private List<TagResponse> tags;

    @Builder
    @QueryProjection
    public StudyQueryResponse(String path, String title, String shortDescription, List<TagResponse> tags) {
        this.path = path;
        this.title = title;
        this.shortDescription = shortDescription;
        this.tags = tags;
    }

    public static StudyQueryResponse of(Study study) {
        return StudyQueryResponse.builder()
                .path(study.getPath())
                .title(study.getTitle())
                .shortDescription(study.getShortDescription())
                .tags(study.getStudyTags().stream()
                        .map(studyTag -> TagResponse.of(studyTag.getTag()))
                        .toList())
                .build();
    }
}
