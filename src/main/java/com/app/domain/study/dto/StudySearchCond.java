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
    private List<Long> zoneIds;
    private List<String> titles;

    @Builder
    public StudySearchCond(List<String> tags, List<Long> zoneIds, List<String> titles) {
        this.tags = tags;
        this.zoneIds = zoneIds;
        this.titles = titles;
    }
}
