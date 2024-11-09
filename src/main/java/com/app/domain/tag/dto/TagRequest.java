package com.app.domain.tag.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter @Setter
public class TagRequest {

    private Set<String> tags;

    @Builder
    public TagRequest(Set<String> tags) {
        this.tags = tags;
    }
}
