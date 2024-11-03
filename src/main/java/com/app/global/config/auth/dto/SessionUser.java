package com.app.global.config.auth.dto;

import com.app.domain.user.User;
import lombok.Getter;

@Getter
public class SessionUser {
    // 인증된 사용자 정보만 필요
    private String name;
    private String email;
    private String picture;

    public SessionUser(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.picture = user.getPicture();
    }
}
