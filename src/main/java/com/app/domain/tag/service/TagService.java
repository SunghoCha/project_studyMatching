package com.app.domain.tag.service;

import com.app.domain.tag.Tag;
import com.app.domain.tag.repository.TagRepository;
import com.app.domain.tag.dto.TagRequest;
import com.app.domain.user.User;
import com.app.domain.user.repository.UserRepository;
import com.app.global.error.ErrorCode;
import com.app.global.error.exception.InvalidTagException;
import com.app.global.error.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    public void initTagData() {
        List<Tag> tags = List.of(new Tag("JavaScript"),
                new Tag("Vue.js"),
                new Tag("React"),
                new Tag("Node.js"),
                new Tag("CSS"),
                new Tag("HTML"),
                new Tag("Spring"),
                new Tag("Java"));
        tagRepository.saveAll(tags);
    }

    public void updateTags(Long userId, TagRequest request) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }

    public boolean isValid(Set<String> userTags) {
        Set<Tag> tags = tagRepository.findByTitleIn(userTags);
        return userTags.size() == tags.size();
    }

    public Set<Tag> findTags(Set<String> tagTitles) {
        Set<Tag> tags = tagRepository.findByTitleIn(tagTitles);
        validate(tagTitles);

        return tags;
    }

    private void validate(Set<String> tagTitles) {
        Set<Tag> tags = tagRepository.findByTitleIn(tagTitles);
        if (tagTitles.size() != tags.size()) {
            throw new InvalidTagException();
        };
    }

    public Set<Tag> getTags(TagRequest request) {
        return new HashSet<>(tagRepository.findAll());
    }

    public void saveAll(List<Tag> tags) {
        // TODO 태그 종복 체크 로직
        tagRepository.saveAll(tags);
    }

    public Tag findByTitle(String title) {
        return tagRepository.findByTitle(title).orElseThrow(InvalidTagException::new);
    }

    public List<Tag> findAll() {
        return tagRepository.findAll();
    }
}
