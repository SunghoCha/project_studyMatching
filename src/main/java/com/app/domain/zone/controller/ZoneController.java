package com.app.domain.zone.controller;


import com.app.domain.zone.dto.ZoneResponse;
import com.app.domain.zone.service.ZoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/zone")
public class ZoneController {

    private final ZoneService zoneService;

    @GetMapping("/allZones")
    public ResponseEntity<Set<ZoneResponse>> getAllZones() {
        Set<ZoneResponse> allZones = zoneService.getAllZones();

        return ResponseEntity.ok(allZones);
    }
}
