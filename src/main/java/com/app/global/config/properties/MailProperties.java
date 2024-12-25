package com.app.global.config.properties;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Slf4j
@Getter @Setter
@Component
@ConfigurationProperties("spring.mail")
public class MailProperties {

    private String username;
    private String password;

    // TODO: 삭제 에정
    @PostConstruct
    public void logProperties() {
        log.info("spring.mail.username: {}", username);
        log.info("spring.mail.password: {}", password);
    }
}
