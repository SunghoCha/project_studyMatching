package com.app.domain.userZone.controller;

import com.app.domain.userZone.dto.UserZoneResponse;
import com.app.domain.userZone.dto.UserZoneUpdateRequest;
import com.app.domain.userZone.dto.UserZoneUpdateResponse;
import com.app.domain.userZone.service.UserZoneService;
import com.app.global.config.auth.LoginUser;
import com.app.global.config.auth.dto.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user-zone")
public class UserZoneController {

    private final UserZoneService userZoneService;

    @GetMapping
    public ResponseEntity<UserZoneResponse> getUserZones(@LoginUser CurrentUser currentUser) {
        UserZoneResponse userZones = userZoneService.findUserZones(currentUser.getId());



        return ResponseEntity.ok(userZones);
    }

    @PatchMapping
    public ResponseEntity<UserZoneUpdateResponse> editUserZones(@LoginUser CurrentUser currentUser,
                                                                @RequestBody UserZoneUpdateRequest request) {
        UserZoneUpdateResponse response = userZoneService.updateUserZones(currentUser.getId(), request);

        return ResponseEntity.ok(response);
    }
}
