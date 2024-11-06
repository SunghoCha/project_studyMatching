package com.app.domain.userTag.controller;

import com.app.domain.userTag.UserTag;
import com.app.domain.userTag.dto.UserTagResponse;
import com.app.domain.userTag.dto.UserTagUpdateRequest;
import com.app.domain.userTag.dto.UserTagUpdateResponse;
import com.app.domain.userTag.service.UserTagService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user-tag")
public class UserTagController {

    private final UserTagService userTagService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserTagResponse> getUserTags(@PathVariable("userId") Long userId) {
        log.info("유저 태그 정보 요청");
        Set<String> userTags = Optional.ofNullable(userTagService.getUserTags(userId))
                .orElse(Collections.emptySet()) // 사용자가 태그설정 처음이면 null일수도 있음
                .stream()
                .map(userTag -> userTag.getTag().getTitle())
                .collect(Collectors.toSet());

        UserTagResponse userTagResponse = UserTagResponse.of(userTags);

        return ResponseEntity.ok(userTagResponse);
    }


    @PatchMapping("/{userId}")
    public ResponseEntity<UserTagUpdateResponse> edit(@PathVariable("userId") Long userId, @RequestBody UserTagUpdateRequest request) {
        UserTagUpdateResponse response = userTagService.update(userId, request);

        return ResponseEntity.ok(response);
    }
}
