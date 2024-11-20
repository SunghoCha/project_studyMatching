package com.app.domain.userTag.dto;

import com.app.domain.userTag.UserTag;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter @Setter
public class UserTagUpdateResponse {
    private List<String> userTags;

    @Builder
    private UserTagUpdateResponse(List<String> userTags) {
        this.userTags = userTags;
    }

    public static UserTagUpdateResponse of(Set<UserTag> userTags) {
        // TODO: 쿼리 최적화
        return UserTagUpdateResponse.builder()
                .userTags(userTags.stream()
                        .map(userTag -> userTag.getTag().getTitle())
                        .toList())
                .build();
    }
}
