package com.app.global.config.auth;

import com.app.global.jwt.TokenType;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Date;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final String accessTokenExpirationTime;
    private final String refreshTokenExpirationTime;
    private final String secretKey;

    public CustomOAuth2SuccessHandler(String accessTokenExpirationTime, String refreshTokenExpirationTime, String secretKey) {
        this.accessTokenExpirationTime = accessTokenExpirationTime;
        this.refreshTokenExpirationTime = refreshTokenExpirationTime;
        this.secretKey = secretKey;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = oauth2Token.getPrincipal();
        String accessToken = createAccessToken(oAuth2User);
        log.info("accessToken 생성 완료");
        String refreshToken = createRefreshToken(oAuth2User);
        log.info("refreshToken 생성 완료");

        String redirectUrlWithToken = "http://localhost:8082/login-success?accessToken=" + accessToken
                + "&refreshToken=" + refreshToken;

        response.sendRedirect(redirectUrlWithToken);
    }

    private String createAccessToken(OAuth2User oAuth2User) {
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(TokenType.ACCESS.name())
                .setIssuedAt(new Date())
                .setExpiration(createAccessTokenExpireTime())
                .claim("role", oAuth2User.getAuthorities())
                .claim("id", oAuth2User.getAttributes().get("id"))
                .claim("email", oAuth2User.getAttributes().get("email"))
                .claim("name", oAuth2User.getAttributes().get("name"))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    private String createRefreshToken(OAuth2User oAuth2User) {
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(TokenType.ACCESS.name())
                .setIssuedAt(new Date())
                .setExpiration(createRefreshTokenExpireTime())
                .claim("email", oAuth2User.getAttributes().get("email"))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    private Date createRefreshTokenExpireTime() {
        return new Date(System.currentTimeMillis() + Long.parseLong(refreshTokenExpirationTime));
    }

    private Date createAccessTokenExpireTime() {
        return new Date(System.currentTimeMillis() + Long.parseLong(accessTokenExpirationTime));
    }
}
