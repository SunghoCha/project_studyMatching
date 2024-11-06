package com.app.domain.tag.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter @Setter
public class TagUpdateRequest {
    private Set<String> tags;

    @Builder
    public TagUpdateRequest(Set<String> tags) {
        this.tags = tags;
    }
}
