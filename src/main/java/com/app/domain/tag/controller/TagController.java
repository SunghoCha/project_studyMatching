package com.app.domain.tag.controller;

import com.app.domain.tag.Tag;
import com.app.domain.tag.dto.TagRequest;
import com.app.domain.tag.dto.TagResponse;
import com.app.domain.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RequestMapping("/tag")
@RestController
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping
    public ResponseEntity<TagResponse> getTags(TagRequest request) {
        Set<Tag> tags = tagService.getTags(request);

        return ResponseEntity.ok(TagResponse.of(tags));
    }
}
