package com.app.domain.study.studyTag.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class StudyTagUpdateRequest {

    private List<String> tags;

    @Builder
    private StudyTagUpdateRequest(List<String> tags) {
        this.tags = tags;
    }
}
