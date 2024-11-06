package com.app.domain.userTag.dto;

import com.app.domain.userTag.UserTag;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter @Setter
public class UserTagUpdateResponse {

    private Set<String> Tags;

    @Builder
    private UserTagUpdateResponse(Set<String> tags) {
        Tags = tags;
    }

    public static UserTagUpdateResponse of(Set<UserTag> userTags) {
        Set<String> tags = userTags.stream()
                .map(userTag -> userTag.getTag().getTitle())
                .collect(Collectors.toSet());

        return UserTagUpdateResponse.builder()
                .tags(tags)
                .build();
    }
}
