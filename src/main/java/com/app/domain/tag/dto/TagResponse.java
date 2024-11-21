package com.app.domain.tag.dto;

import com.app.domain.tag.Tag;
import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.util.Set;
import java.util.stream.Collectors;

@Getter @Setter
@NoArgsConstructor
public class TagResponse {
    // TODO 필드값 List 형태로 변환
    private String tag;

    @Builder
    @QueryProjection
    public TagResponse(String tag) {
        this.tag = tag;
    }

    public static TagResponse of(Tag tag) {
        return TagResponse.builder()
                .tag(tag.getTitle())
                .build();
    }
}
