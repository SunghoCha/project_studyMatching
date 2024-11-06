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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserTagService {

    private final UserTagRepository userTagRepository;
    private final UserRepository userRepository;
    private final TagService tagService;

    public UserTagUpdateResponse update(Long userId, UserTagUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(ErrorCode.MEMBER_NOT_EXISTS));

        Set<String> tagTitles = request.getUserTags();
        // 유효한 태그인지 확인
        tagService.validate(tagTitles);

        // 새로운 태그들만 필터랑
        List<String> existingTagTitles = user.getUserTags().stream()
                .map(userTag -> userTag.getTag().getTitle()).toList();

        Set<String> newTagTitle = tagTitles.stream()
                .filter(tagTitle -> !existingTagTitles.contains(tagTitle))
                .collect(Collectors.toSet());

        // 새로운 태그 기반으로 유저태그 생성
        Set<UserTag> newUserTags = tagService.findTags(newTagTitle).stream()
                .map(tag -> UserTag.builder()
                        .user(user)
                        .tag(tag)
                        .build())
                .collect(Collectors.toSet());

        // 연관관계 편의 메서드
        user.addUserTags(newUserTags);
        userTagRepository.saveAll(newUserTags);

        return UserTagUpdateResponse.of(user.getUserTags());
    }

    public Set<UserTag> getUserTags(Long userId) {
        return userTagRepository.findByUserId(userId);
    }
}
