package com.app.domain.userZone.controller;

import com.app.domain.userZone.dto.UserZoneResponse;
import com.app.domain.userZone.dto.UserZoneUpdateRequest;
import com.app.domain.userZone.service.UserZoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-zone")
public class UserZoneController {

    private final UserZoneService userZoneService;


    @GetMapping("/{userId}")
    public ResponseEntity<Set<UserZoneResponse>> getUserZones(@PathVariable(name = "userId") Long userId) {
        Set<UserZoneResponse> userZones = userZoneService.getUserZones(userId);

        return ResponseEntity.ok(userZones);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Set<UserZoneResponse>> editUserZones(@PathVariable(name = "userId") Long userId,
                                                               @RequestBody Set<UserZoneUpdateRequest> request) {
        Set<UserZoneResponse> response = userZoneService.updateUserZones(userId, request);

        return ResponseEntity.ok(response);
    }
}
