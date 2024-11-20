package com.app.domain.study.studyTag.dto;

import com.app.domain.study.studyTag.StudyTag;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter @Setter
public class StudyTagCreateResponse {

    private List<String> tags;

    @Builder
    private StudyTagCreateResponse(List<String> tags) {
        this.tags = tags;
    }

    public static StudyTagCreateResponse of(Set<StudyTag> studyTags) {
        // TODO: 쿼리 최적화
        return StudyTagCreateResponse.builder()
                .tags(studyTags.stream()
                        .map(studyTag -> studyTag.getTag().getTitle())
                        .toList())
                .build();

    }
}
