package com.app.api.login;

import com.app.domain.user.User;
import com.app.domain.user.constant.Role;
import com.app.domain.user.repository.UserRepository;
import com.app.global.error.exception.UserNotFoundException;
import com.app.global.jwt.TokenType;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.function.ServerRequest;

import java.util.Date;

import static java.nio.charset.StandardCharsets.UTF_8;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final UserRepository userRepository;

    @Value("${token.access-token-expiration-time}")
    private String accessTokenExpirationTime;

    @Value("${token.refresh-token-expiration-time}")
    private String refreshTokenExpirationTime;

    @Value("${token.secret}")
    private String secretKey;

    @GetMapping("/")
    public String home() {
        return "welcome";
    }

    @GetMapping("/free-login")
    public ResponseEntity<Void> adminLogin() {
        User user = userRepository.findByEmail("admin@example.com").orElseThrow(UserNotFoundException::new);

        String accessToken = createAccessToken(user);
        String refreshToken = createRefreshToken(user);

        // TODO: url 변수화
        String redirectUrlWithToken = "http://localhost:82/login-success?accessToken=" + accessToken
                + "&refreshToken=" + refreshToken;

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Location", redirectUrlWithToken);

        return new ResponseEntity<>(httpHeaders, HttpStatus.FOUND);
    }

    private String createAccessToken(User user) {
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(TokenType.ACCESS.name())
                .setIssuedAt(new Date())
                .setExpiration(createAccessTokenExpireTime())
                .claim("role", Role.ADMIN)
                .claim("id", user.getId())
                .claim("email", user.getEmail())
                .claim("name", user.getName())
                .claim("picture", user.getPicture())
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    private String createRefreshToken(User user) {
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(TokenType.ACCESS.name())
                .setIssuedAt(new Date())
                .setExpiration(createRefreshTokenExpireTime())
                .claim("email", user.getEmail())
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    private Date createRefreshTokenExpireTime() {
        return new Date(System.currentTimeMillis() + Long.parseLong(refreshTokenExpirationTime));
    }

    private Date createAccessTokenExpireTime() {
        return new Date(System.currentTimeMillis() + Long.parseLong(accessTokenExpirationTime) + 1000000000000000L );
    }


}
