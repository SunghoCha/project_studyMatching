package com.app.global.config.jpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Optional;

@Slf4j
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails) {
                String username = ((UserDetails) principal).getUsername();
                log.info("현재 Auditor는 UserDetails입니다. : {}", username);
                return Optional.of(username);
            }
            if (principal instanceof OAuth2User) {
                Object nameAttribute = ((OAuth2User) principal).getAttributes().get("name");
                if (nameAttribute instanceof String username) {
                    log.info("현재 Auditor는 OAuth2User입니다. : {}", username);
                    return Optional.of(username); // String 체크까지 해줘야하나? 아닌 경우 처리 로직도 없는 상태.. 그렇다고 더하면 과한느낌?
                }
            }
        }
        log.info("인증되지 않은 유저입니다. : Unknown");
        return Optional.of("Unknown");
    }
}
