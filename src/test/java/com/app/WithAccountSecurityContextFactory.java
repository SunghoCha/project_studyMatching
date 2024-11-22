package com.app;


import com.app.domain.user.User;
import com.app.domain.user.service.UserService;
import com.app.global.config.auth.dto.OAuthAttributes;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class WithAccountSecurityContextFactory implements WithSecurityContextFactory<WithAccount> {

    private final UserService userService;

    @Override
    public SecurityContext createSecurityContext(WithAccount annotation) {

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(annotation.role());
        Collection<SimpleGrantedAuthority> authorities = List.of(authority);

        String registrationId = "test";
        String nameAttributeKey = "nameAttributeKey";

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("name", annotation.name());
        attributes.put("email", annotation.email());
        attributes.put("picture", "picture");
        attributes.put("nameAttributeKey", nameAttributeKey);

        OAuthAttributes oAuthAttributes = OAuthAttributes.builder()
                .attributes(attributes)
                .email((String) attributes.get("email"))
                .name((String) attributes.get("name"))
                .picture((String) attributes.get("picture"))
                .build();

        User user = userService.saveOrUpdate(oAuthAttributes);
        oAuthAttributes.setUserId(user.getId());
        DefaultOAuth2User oAuth2User = new DefaultOAuth2User(
                authorities,
                oAuthAttributes.getAttributes(),
                nameAttributeKey);

        OAuth2AuthenticationToken authenticationToken =
                new OAuth2AuthenticationToken(oAuth2User, authorities, registrationId);

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authenticationToken);

        return securityContext;
    }
}
