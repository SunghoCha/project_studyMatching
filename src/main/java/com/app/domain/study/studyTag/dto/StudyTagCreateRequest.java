package com.app.domain.study.studyTag.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter @Setter
@NoArgsConstructor
public class StudyTagCreateRequest {

    private Set<String> tags;

    @Builder
    private StudyTagCreateRequest(Set<String> tags) {
        this.tags = tags;
    }
}
