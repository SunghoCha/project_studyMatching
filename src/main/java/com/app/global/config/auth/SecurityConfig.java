package com.app.global.config.auth;

import com.app.domain.user.constant.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;
    private final CustomOauth2UserService customOAuth2UserService;
    private final CustomRequestLoggingFilter requestLoggingFilter;
    private final NimbusJwtDecoder customJwtDecoder;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authRequest -> authRequest
                        //.requestMatchers("/api/**").hasAnyAuthority("SCOPE_profile", "SCOPE_email")
                        .requestMatchers("/guest").hasRole(Role.GUEST.name())
                        .requestMatchers("/user").hasRole(Role.USER.name())
                        .requestMatchers("/admin").hasRole(Role.ADMIN.name())
                        .requestMatchers("/authenticated").authenticated()
                        .requestMatchers("/**").permitAll())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin) // 클릭채킹 방어 (~h2 db용으로 설정)
                        .xssProtection(Customizer.withDefaults())) // xss
                .logout(logout -> logout.logoutSuccessUrl("/"))
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                                .userService(customOAuth2UserService))
                        .successHandler(customOAuth2SuccessHandler)
                .defaultSuccessUrl("/test")) // 로그인성공 후 임시 경로.
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(customJwtDecoder))) // jwt 토큰을 검증하는 빈들과 클래스를 생성하고 초기화함
                .addFilterBefore(requestLoggingFilter, OAuth2LoginAuthenticationFilter.class);
        ;

        return http.build();
    }
}
