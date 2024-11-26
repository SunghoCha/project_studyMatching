package com.app.domain.study.studyTag.dto;

import com.app.domain.study.studyTag.StudyTag;
import com.app.domain.study.studyZone.StudyZone;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter @Setter
public class StudyTagUpdateResponse {

    private List<String> tags;

    @Builder
    public StudyTagUpdateResponse(List<String> tags) {
        this.tags = tags;
    }

    public static StudyTagUpdateResponse of(Set<StudyTag> studyTags) {

        return StudyTagUpdateResponse.builder()
                .tags(studyTags.stream()
                        .sorted(Comparator.comparing(tag -> tag.getTag().getTitle()))
                        .map(studyTag -> studyTag.getTag().getTitle())
                        .toList())
                .build();
    }
}
