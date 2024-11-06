package com.app.domain.tag.service;

import com.app.domain.tag.Tag;
import com.app.domain.tag.repository.TagRepository;
import com.app.domain.tag.dto.TagUpdateRequest;
import com.app.domain.user.User;
import com.app.domain.user.repository.UserRepository;
import com.app.global.error.ErrorCode;
import com.app.global.error.exception.InvalidTagException;
import com.app.global.error.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    public void updateTags(Long userId, TagUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(ErrorCode.MEMBER_NOT_EXISTS));
    }

    public boolean isValid(Set<String> userTags) {
        Set<Tag> tags = tagRepository.findByTitleIn(userTags);
        return userTags.size() == tags.size();
    }

    public Set<Tag> findTags(Set<String> tagTitles) {
        Set<Tag> tags = tagRepository.findByTitleIn(tagTitles);
        if (tags.size() != tagTitles.size()) {
            throw new InvalidTagException(ErrorCode.INVALID_TAG);
        }
        return tags;
    }

    public void validate(Set<String> tagTitles) {
        Set<Tag> tags = tagRepository.findByTitleIn(tagTitles);
        if (tagTitles.size() != tags.size()) {
            throw new InvalidTagException(ErrorCode.INVALID_TAG);
        };
    }
}
