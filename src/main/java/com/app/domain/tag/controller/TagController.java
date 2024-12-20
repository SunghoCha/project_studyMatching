package com.app.domain.tag.controller;

import com.app.domain.tag.Tag;
import com.app.domain.tag.dto.TagResponse;
import com.app.domain.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RequestMapping("/tag")
@RestController
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping
    public ResponseEntity<List<TagResponse>> getTags() {
        Set<Tag> tags = tagService.getTags();
        List<TagResponse> tagResponses = tags.stream()
                .map(TagResponse::of)
                .toList();
        // TODO: 추후 삭제
        for (TagResponse tagResponse : tagResponses) {
            System.out.println("tagResponse.getTag(). = " + tagResponse.getTag());
        }

        return ResponseEntity.ok(tagResponses);
    }
}
