package com.app.domain.tag.dto;

import com.app.domain.tag.Tag;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter @Setter
public class TagResponse {

    private String tag;

    @Builder
    private TagResponse(String tag) {
        this.tag = tag;
    }

    public static TagResponse of(Tag tag) {
        return TagResponse.builder()
                .tag(tag.getTitle())
                .build();
    }
}
