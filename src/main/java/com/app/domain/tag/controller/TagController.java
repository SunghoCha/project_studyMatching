package com.app.domain.tag.controller;

import com.app.domain.tag.Tag;
import com.app.domain.tag.dto.TagRequest;
import com.app.domain.tag.dto.TagResponse;
import com.app.domain.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequestMapping("/tag")
@RestController
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping
    public ResponseEntity<List<TagResponse>> getTags(TagRequest request) {
        Set<Tag> tags = tagService.getTags(request);
        List<TagResponse> tagResponses = tags.stream()
                .map(TagResponse::of)
                .toList();

        return ResponseEntity.ok(tagResponses);
    }
}
