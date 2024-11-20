package com.app.domain.study.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
public class StudySearchCond {

    private List<String> tags;
    private List<String> zones;

    @Builder
    public StudySearchCond(List<String> tags, List<String> zones) {
        this.tags = tags;
        this.zones = zones;
    }
}
