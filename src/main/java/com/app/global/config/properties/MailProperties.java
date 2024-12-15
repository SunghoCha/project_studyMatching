package com.app.global.config.properties;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter @Setter
@Component
@ConfigurationProperties("spring.mail")
public class MailProperties {

    private String username;
    private String password;

    @PostConstruct
    public void logProperties() {
        System.out.println("spring.mail.username: " + username);
        System.out.println("spring.mail.password: " + password);
    }
}
