package com.app.api.login.test;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
public class TestResponse {

    private final Collection<GrantedAuthority> authorities;
    private final String accessToken;

    @Builder
    public TestResponse(Collection<GrantedAuthority> authorities, String accessToken) {
        this.authorities = authorities;
        this.accessToken = accessToken;
    }

    public static TestResponse of(OAuth2AuthenticationToken oauthToken, String tokenValue) {
        return TestResponse.builder()
                .accessToken(tokenValue)
                .authorities(oauthToken.getAuthorities())
                .build();
    }

    @Override
    public String toString() {
        return "TestResponse{" +
                "authorities=" + authorities +
                ", accessToken='" + accessToken + '\'' +
                '}';
    }
}
