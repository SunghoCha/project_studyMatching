package com.app.domain.study.dto;

import com.app.domain.study.Study;
import com.app.domain.tag.dto.TagResponse;
import com.app.domain.zone.dto.ZoneResponse;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter @Setter
public class StudyQueryResponse {

    private String path;
    private String title;
    private String shortDescription;
    private Set<TagResponse> tags;
    private Set<ZoneResponse> zones;

    @Builder
    @QueryProjection
    public StudyQueryResponse(String path, String title, String shortDescription, Set<TagResponse> tags, Set<ZoneResponse> zones) {
        this.path = path;
        this.title = title;
        this.shortDescription = shortDescription;
        this.tags = tags;
        this.zones= zones;
    }

    public static StudyQueryResponse of(Study study) {
        return StudyQueryResponse.builder()
                .path(study.getPath())
                .title(study.getTitle())
                .shortDescription(study.getShortDescription())
                .tags(study.getStudyTags().stream()
                        .map(studyTag -> TagResponse.of(studyTag.getTag()))
                        .collect(Collectors.toSet()))
                .zones(study.getStudyZones().stream()
                        .map(studyZone -> ZoneResponse.of(studyZone.getZone()))
                        .collect(Collectors.toSet()))
                .build();
    }
}
