package com.app.api.login.test;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;

@Controller
@RequiredArgsConstructor
public class LoginTestController {

    private final OAuth2AuthorizedClientService authorizedClientService;
    @Value("${token.access-token-expiration-time}")
    private String accessTokenExpirationTime;

    @GetMapping("/test/login")
    public String loginTest() {
        return "loginForm.html";
    }


    @ResponseBody
    @GetMapping("/test")
    public ResponseEntity<TestResponse> requestTest() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;

            OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                    oauthToken.getAuthorizedClientRegistrationId(), oauthToken.getName());
            Collection<? extends GrantedAuthority> authorities = oauthToken.getPrincipal().getAuthorities();
            TestResponse response = TestResponse.of(oauthToken, authorizedClient.getAccessToken().getTokenValue());
            return ResponseEntity.ok(response);
        }
        return null;
    }


    @ResponseBody
    @GetMapping("/authenticated")
    public String authenticated() {
        return "authenticated";
    }

    @ResponseBody
    @GetMapping("/guest")
    public String request() {

        return "guest";
    }

    @ResponseBody
    @GetMapping("/yml-test")
    public String ymlTest() {

        return accessTokenExpirationTime;
    }
}
