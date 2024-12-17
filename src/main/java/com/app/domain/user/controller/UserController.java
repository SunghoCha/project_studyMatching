package com.app.domain.user.controller;

import com.app.domain.user.dto.UserNotificationUpdateRequest;
import com.app.domain.user.service.UserService;
import com.app.global.config.auth.LoginUser;
import com.app.global.config.auth.dto.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PatchMapping("/notifications")
    public ResponseEntity<Void> updateNotificationSettings(@LoginUser CurrentUser currentUser,
                                                           @RequestBody UserNotificationUpdateRequest request) {
        userService.updateNotificationSettings(currentUser.getId(), request);
        return ResponseEntity.ok().build();
    }
}
