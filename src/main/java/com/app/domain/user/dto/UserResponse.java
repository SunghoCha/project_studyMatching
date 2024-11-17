package com.app.domain.user.dto;

import com.app.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserResponse {

    private String name;
    private String email;

    @Builder
    public UserResponse(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public static UserResponse of(User user) {
        return UserResponse.builder()
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
