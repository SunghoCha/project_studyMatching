package com.app.domain.tag.dto;

import com.app.domain.tag.Tag;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter @Setter
public class TagResponse {

    private Set<String> tags;

    @Builder
    private TagResponse(Set<String> tags) {
        this.tags = tags;
    }

    public static TagResponse of(Set<Tag> tags) {
        return TagResponse.builder()
                .tags(tags.stream()
                        .map(Tag::getTitle)
                        .collect(Collectors.toSet()))
                .build();
    }
}
