package com.app.domain.study.studyTag.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class StudyTagCreateRequest {

    private List<String> tags;

    @Builder
    private StudyTagCreateRequest(List<String> tags) {
        this.tags = tags;
    }
}
