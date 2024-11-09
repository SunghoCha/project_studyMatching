package com.app.domain.userTag.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter @Setter
@NoArgsConstructor
public class UserTagUpdateRequest {
    private  Set<String> tags;

    @Builder
    public UserTagUpdateRequest(Set<String> tags) {
        this.tags = tags;
    }
}
