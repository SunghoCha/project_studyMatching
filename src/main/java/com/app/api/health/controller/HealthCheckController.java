package com.app.api.health.controller;

import com.app.api.health.dto.HealthCheckResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HealthCheckController {

    private final Environment env;

    @GetMapping("/health")
    public ResponseEntity<HealthCheckResponseDto> healthCheck() {
        HealthCheckResponseDto responseDto = HealthCheckResponseDto.builder()
                .health("ok")
                .activeProfiles(List.of(env.getActiveProfiles()))
                .build();

        return ResponseEntity.ok(responseDto);
    }

}
