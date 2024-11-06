package com.app.domain.tag.controller;

import com.app.domain.tag.dto.TagUpdateRequest;
import com.app.domain.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/tag")
@RestController
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @PostMapping("/edit/{userId}")
    public void editTags(@PathVariable("userId") Long userId, TagUpdateRequest request) {
        tagService.updateTags(userId, request);
    }

}
