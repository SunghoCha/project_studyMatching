package com.app.domain.study.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
public class SearchCond {

    private List<String> tags;
    private List<Long> zoneIds;
    private String titles;

    @Builder
    public SearchCond(List<String> tags, List<Long> zoneIds, String titles) {
        this.tags = tags;
        this.zoneIds = zoneIds;
        this.titles = titles;
    }
}
