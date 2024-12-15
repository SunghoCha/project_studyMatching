package com.app.domain.user.userTag.service;

import com.app.domain.tag.Tag;
import com.app.domain.tag.service.TagService;
import com.app.domain.user.User;
import com.app.domain.user.repository.UserRepository;
import com.app.domain.user.userTag.repository.UserTagRepository;
import com.app.domain.user.userTag.UserTag;
import com.app.domain.user.userTag.dto.UserTagResponse;
import com.app.domain.user.userTag.dto.UserTagUpdateRequest;
import com.app.domain.user.userTag.dto.UserTagUpdateResponse;
import com.app.global.error.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserTagService {

    private final UserTagRepository userTagRepository;
    private final UserRepository userRepository;
    private final TagService tagService;

    public UserTagUpdateResponse update(Long userId, UserTagUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        Set<Tag> tags = tagService.findTags(request.getTags());

        Set<UserTag> userTags = tags.stream()
                .map(tag -> UserTag.builder()
                        .user(user)
                        .tag(tag)
                        .build())
                .collect(Collectors.toSet());

        userTagRepository.deleteAllByUserId(userId);
        // 연관관계 편의 메서드
        user.setUserTags(userTags);
        userTagRepository.saveAll(userTags);

        return UserTagUpdateResponse.of(userTags);
    }

    public UserTagResponse findUserTags(Long userId) {

        List<String> userTags = Optional.ofNullable(userTagRepository.findByUserId(userId))
                .orElse(Collections.emptyList()) // 사용자가 태그설정 처음이면 null일수도 있음
                .stream()
                .map(userTag -> userTag.getTag().getTitle())
                .toList();

        return UserTagResponse.of(userTags);
    }

    public List<UserTag> getUserTags(Long userId) {

        return userTagRepository.findByUserId(userId);
    }
}
