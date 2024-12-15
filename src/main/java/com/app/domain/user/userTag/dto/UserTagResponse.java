package com.app.domain.user.userTag.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
public class UserTagResponse {
    // TODO 필드값 List 형태로 변환
    private List<String> userTags;

    @Builder
    private UserTagResponse(List<String> userTags) {
        this.userTags = userTags;
    }

    public static UserTagResponse of(List<String> userTags) {
        return UserTagResponse.builder()
                .userTags(userTags)
                .build();
    }
}
