package com.app.domain.userTag.service;

import com.app.domain.tag.Tag;
import com.app.domain.tag.repository.TagRepository;
import com.app.domain.tag.service.TagService;
import com.app.domain.user.User;
import com.app.domain.user.repository.UserRepository;
import com.app.domain.userTag.UserTag;
import com.app.domain.userTag.dto.UserTagUpdateRequest;
import com.app.domain.userTag.dto.UserTagUpdateResponse;
import com.app.domain.userTag.repository.UserTagRepository;
import com.app.global.error.ErrorCode;
import com.app.global.error.exception.InvalidTagException;
import com.app.global.error.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    public Set<UserTag> getUserTags(Long userId) {
        return userTagRepository.findByUserId(userId);
    }
}
