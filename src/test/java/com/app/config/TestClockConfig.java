package com.app.config;

import com.app.TestUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class TestClockConfig {

    @Bean
    public Clock clock() {
        return TestUtils.getFixedClock();
    }
}
