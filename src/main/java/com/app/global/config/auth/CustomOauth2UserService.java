package com.app.global.config.auth;

import com.app.domain.user.User;
import com.app.domain.user.repository.UserRepository;
import com.app.domain.user.service.UserService;
import com.app.global.config.auth.dto.OAuthAttributes;
import com.app.global.config.auth.dto.SessionUser;
import com.app.global.error.JsonSerializationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomOauth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserService userService;
    private final HttpSession httpSession;
    private final ObjectMapper objectMapper;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        ClientRegistration registration = userRequest.getClientRegistration();
        String registrationId = registration.getRegistrationId();
        String userNameAttributeName = registration.getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        // registration이나 userNameAttributeName이 여기서만 쓰이는데 인자로 userRequest와 oAuth2User를 전달하는건 별로인가?
        // 이렇게 되면 dto가 단순한 역할을 넘어서는 느낌이라 이렇게 하는게 나을지도..
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        User user = userService.saveOrUpdate(attributes);
        log.info("유저 정보 생성 또는 업데이트 완료");
        // 무상태 코드로 바꿀 예정
        String userJson = convertToJson(new SessionUser(user));
        httpSession.setAttribute("user", userJson);
        // 싱글톤으로 하면 불변이라 권한 추가 불가능함에 유의
        // 토큰기반 무상태 방식으로 하고 @AuthenticationPrincipal로 DefaultOAuth2User 정보 가져오는 방식으로 해야할듯
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }

    private String convertToJson(SessionUser sessionUser) {
        String userJson;
        try {
            userJson = objectMapper.writeValueAsString(sessionUser);
        } catch (JsonProcessingException e) {
            throw new JsonSerializationException("JSON 직렬화 실패");
        }
        return userJson;
    }
}
