package com.app.domain.user.userTag.controller;

import com.app.domain.user.userTag.dto.UserTagResponse;
import com.app.domain.user.userTag.dto.UserTagUpdateRequest;
import com.app.domain.user.userTag.dto.UserTagUpdateResponse;
import com.app.domain.user.userTag.service.UserTagService;
import com.app.global.config.auth.LoginUser;
import com.app.global.config.auth.dto.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user-tag")
public class UserTagController {

    private final UserTagService userTagService;

    @GetMapping
    public ResponseEntity<UserTagResponse> findUserTags(@LoginUser CurrentUser currentUser) {
        log.info("유저 태그 정보 요청");
        // TODO: 컨트롤러에 엔티티 정보 노출된 것 리팩토링
        UserTagResponse response = userTagService.findUserTags(currentUser.getId());

        return ResponseEntity.ok(response);
    }

    @PatchMapping
    public ResponseEntity<UserTagUpdateResponse> editUserTags(@LoginUser CurrentUser currentUser, @RequestBody UserTagUpdateRequest request) {
        log.info("유저 태그 업데이트 요청");
        log.info(request.getTags().toString());
        UserTagUpdateResponse response = userTagService.update(currentUser.getId(), request);

        return ResponseEntity.ok(response);
    }
}
