package com.app.domain.userTag.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter @Setter
@NoArgsConstructor
public class UserTagResponse {

    private Set<String> userTags;

    @Builder
    private UserTagResponse(Set<String> userTags) {
        this.userTags = userTags;
    }

    public static UserTagResponse of(Set<String> userTags) {
        return UserTagResponse.builder()
                .userTags(userTags)
                .build();
    }
}
