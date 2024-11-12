package com.app.global.config.auth.dto;

import com.app.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

@Getter
public class CurrentUser {
    // 인증된 사용자 정보만 필요
    private Long id;
    private String name;
    private String email;
    private String picture;

    @Builder
    public CurrentUser(Long id, String name, String email, String picture) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.picture = picture;
    }

    public static CurrentUser of(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        return CurrentUser.builder()
                .id((Long) attributes.get("id"))
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .build();
    }
}
