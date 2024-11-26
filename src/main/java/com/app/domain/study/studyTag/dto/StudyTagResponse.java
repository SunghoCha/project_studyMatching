package com.app.domain.study.studyTag.dto;

import com.app.domain.study.studyTag.StudyTag;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter @Setter
public class StudyTagResponse {

    private List<String> tags;

    @Builder
    public StudyTagResponse(List<String> tags) {
        this.tags = tags;
    }

    public static StudyTagResponse of(List<StudyTag> studyTags) {

        return StudyTagResponse.builder()
                .tags(studyTags.stream()
                        .map(studyTag -> studyTag.getTag().getTitle())
                        .toList())
                .build();
    }
}
