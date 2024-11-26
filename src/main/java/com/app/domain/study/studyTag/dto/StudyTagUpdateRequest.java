package com.app.domain.study.studyTag.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter @Setter
@NoArgsConstructor
public class StudyTagUpdateRequest {

    private Set<String> tags;

    @Builder
    private StudyTagUpdateRequest(Set<String> tags) {
        this.tags = tags;
    }
}
